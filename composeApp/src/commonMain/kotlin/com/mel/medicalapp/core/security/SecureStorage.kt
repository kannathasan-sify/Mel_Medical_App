package com.mel.medicalapp.core.security

/**
 * Cross-platform interface for storing encrypted tokens, refresh tokens, and session keys.
 * Android: EncryptedSharedPreferences (Android Keystore / Tink)
 * iOS: Keychain Services with kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
 */
expect class SecureStorage() {
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun remove(key: String)
    suspend fun clearAll()
}

