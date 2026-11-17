package com.mel.medicalapp.core.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import java.security.MessageDigest
import java.util.UUID

private const val TAG = "MelGoogleAuth"

class AndroidGoogleAuthHandler(
    private val context: Context,
    private val serverClientId: String,
    private val scope: CoroutineScope,
    private val onSuccess: (GoogleAuthUser) -> Unit,
    private val onError: (String) -> Unit
) : GoogleAuthHandler {

    private val credentialManager = CredentialManager.create(context)

    private fun findActivity(ctx: Context): Activity? {
        var current = ctx
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }

    override suspend fun signInWithGoogle(): Result<GoogleAuthUser> {
        val activity = findActivity(context)
        val targetContext = activity ?: context

        val candidates = listOfNotNull(
            serverClientId,
            com.mel.medicalapp.core.config.AppConfig.GOOGLE_SERVER_CLIENT_ID,
            com.mel.medicalapp.core.config.AppConfig.GOOGLE_ANDROID_CLIENT_ID
        ).distinct()

        Log.d(TAG, "Initiating Google Sign-In with candidate client IDs: $candidates")

        var lastException: Exception? = null

        for (clientId in candidates) {
            // Attempt 1: Standard ID Token request without nonce
            val attempt1 = attemptSignInWithoutNonce(targetContext, clientId)
            if (attempt1.isSuccess) {
                val user = attempt1.getOrThrow()
                Log.d(TAG, "Google Sign-In succeeded with client ID: $clientId")
                onSuccess(user)
                return attempt1
            }

            val err1 = attempt1.exceptionOrNull()
            if (err1 is GetCredentialCancellationException) {
                onError("Sign-in cancelled by user.")
                return Result.failure(err1)
            }
            lastException = err1 as? Exception ?: lastException

            // Attempt 2: With SHA-256 hashed nonce
            val attempt2 = attemptSignIn(targetContext, clientId)
            if (attempt2.isSuccess) {
                val user = attempt2.getOrThrow()
                Log.d(TAG, "Google Sign-In succeeded with hashed nonce on client ID: $clientId")
                onSuccess(user)
                return attempt2
            }
            if (attempt2.exceptionOrNull() is GetCredentialCancellationException) {
                onError("Sign-in cancelled by user.")
                return attempt2
            }
            lastException = attempt2.exceptionOrNull() as? Exception ?: lastException
        }

        val finalErrMsg = lastException?.message ?: "Google Developer Console error [28444]"
        Log.e(TAG, "Google Sign-In failed across all candidate IDs: $finalErrMsg", lastException)

        val userFacingMsg = if (finalErrMsg.contains("28444")) {
            "Google Config Error [28444]: Please add a 'Web application' Client ID in Google Cloud Console -> Clients, and put its ID in AppConfig.kt."
        } else {
            "Google Sign-In Error: $finalErrMsg"
        }

        onError(userFacingMsg)
        return Result.failure(lastException ?: Exception(finalErrMsg))
    }

    private suspend fun attemptSignIn(ctx: Context, clientId: String): Result<GoogleAuthUser> {
        return try {
            val rawNonce = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val hashedNonce = md.digest(rawNonce.toByteArray()).fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(request = request, context = ctx)
            parseCredential(response.credential)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun attemptSignInWithoutNonce(ctx: Context, clientId: String): Result<GoogleAuthUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(request = request, context = ctx)
            parseCredential(response.credential)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCredential(credential: androidx.credentials.Credential): Result<GoogleAuthUser> {
        return if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
            Result.success(
                GoogleAuthUser(
                    idToken = googleIdToken.idToken,
                    email = googleIdToken.id,
                    displayName = googleIdToken.displayName ?: googleIdToken.id,
                    photoUrl = googleIdToken.profilePictureUri?.toString()
                )
            )
        } else {
            Result.failure(Exception("Unsupported credential type: ${credential.type}"))
        }
    }

    override suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(
                androidx.credentials.ClearCredentialStateRequest()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
        }
    }
}

@Composable
actual fun rememberGoogleAuthHandler(
    serverClientId: String,
    onSuccess: (GoogleAuthUser) -> Unit,
    onError: (String) -> Unit
): GoogleAuthHandler {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    return remember(serverClientId) {
        AndroidGoogleAuthHandler(
            context = context,
            serverClientId = serverClientId,
            scope = scope,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}
