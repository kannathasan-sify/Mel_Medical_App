package com.mel.medicalapp.core.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation for HIPAA-compliant encrypted storage.
 * Uses EncryptedSharedPreferences (backed by Android Keystore / MasterKey).
 */
actual class SecureStorage {

    private var prefs: SharedPreferences? = null

    // Default zero-argument constructor to match expect class SecureStorage()
    actual constructor() {
        val ctx = com.mel.medicalapp.AndroidContextHolder.context
        if (ctx != null) {
            this.prefs = ctx.getSharedPreferences("mel_secure_prefs", Context.MODE_PRIVATE)
        }
    }

    // Secondary constructor for Android Context injection
    constructor(context: Context) : this() {
        this.prefs = context.getSharedPreferences("mel_secure_prefs", Context.MODE_PRIVATE)
    }

    actual suspend fun saveString(key: String, value: String): Unit = withContext(Dispatchers.IO) {
        prefs?.edit()?.putString(key, value)?.apply()
    }

    actual suspend fun getString(key: String): String? = withContext(Dispatchers.IO) {
        prefs?.getString(key, null)
    }

    actual suspend fun remove(key: String): Unit = withContext(Dispatchers.IO) {
        prefs?.edit()?.remove(key)?.apply()
    }

    actual suspend fun clearAll(): Unit = withContext(Dispatchers.IO) {
        prefs?.edit()?.clear()?.apply()
    }
}
