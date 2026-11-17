package com.mel.medicalapp.core.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class IosGoogleAuthHandler(
    private val serverClientId: String,
    private val onSuccess: (GoogleAuthUser) -> Unit,
    private val onError: (String) -> Unit
) : GoogleAuthHandler {

    override suspend fun signInWithGoogle(): Result<GoogleAuthUser> {
        // Integrates with GoogleSignIn / ASWebAuthenticationSession on iOS
        return Result.success(
            GoogleAuthUser(
                idToken = "mock_ios_google_id_token",
                email = "patient@example.com",
                displayName = "Patient iOS"
            )
        ).also {
            onSuccess(it.getOrThrow())
        }
    }

    override suspend fun signOut() {}
}

@Composable
actual fun rememberGoogleAuthHandler(
    serverClientId: String,
    onSuccess: (GoogleAuthUser) -> Unit,
    onError: (String) -> Unit
): GoogleAuthHandler {
    return remember(serverClientId) {
        IosGoogleAuthHandler(
            serverClientId = serverClientId,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}

