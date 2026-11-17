package com.mel.medicalapp.core.auth

import androidx.compose.runtime.Composable

data class GoogleAuthUser(
    val idToken: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String? = null
)

interface GoogleAuthHandler {
    suspend fun signInWithGoogle(): Result<GoogleAuthUser>
    suspend fun signOut()
}

@Composable
expect fun rememberGoogleAuthHandler(
    serverClientId: String,
    onSuccess: (GoogleAuthUser) -> Unit,
    onError: (String) -> Unit
): GoogleAuthHandler

