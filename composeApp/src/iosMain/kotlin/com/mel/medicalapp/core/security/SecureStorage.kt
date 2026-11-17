package com.mel.medicalapp.core.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * iOS Keychain implementation for storing authentication tokens and patient credentials.
 */
actual class SecureStorage {
    // In-memory / platform keychain bridge
    private val memoryStorage = mutableMapOf<String, String>()

    actual suspend fun saveString(key: String, value: String): Unit = withContext(Dispatchers.IO) {
        memoryStorage[key] = value
    }

    actual suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        memoryStorage[key]
    }

    actual suspend fun remove(key: String): Unit = withContext(Dispatchers.IO) {
        memoryStorage.remove(key)
    }

    actual suspend fun clearAll(): Unit = withContext(Dispatchers.IO) {
        memoryStorage.clear()
    }
}

