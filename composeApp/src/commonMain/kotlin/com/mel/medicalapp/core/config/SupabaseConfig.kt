package com.mel.medicalapp.core.config

/**
 * Backward-compatibility alias redirecting to centralized AppConfig.
 */
object SupabaseConfig {
    val SUPABASE_URL get() = AppConfig.SUPABASE_URL
    val SUPABASE_ANON_KEY get() = AppConfig.SUPABASE_ANON_KEY
    val REST_URL get() = AppConfig.REST_URL
    val AUTH_URL get() = AppConfig.AUTH_URL
    val STORAGE_URL get() = AppConfig.STORAGE_URL
}
