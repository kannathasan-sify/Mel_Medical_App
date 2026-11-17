package com.mel.medicalapp.core.config

/**
 * Centralized Application Configuration.
 * Reused everywhere across authentication, Supabase, and backend APIs.
 */
object AppConfig {
    // ------------------------------------------------------------------------
    // GOOGLE OAUTH CLIENT IDS
    // ------------------------------------------------------------------------
    /**
     * Web Client ID (used as serverClientId for Google Credential Manager & Supabase exchange).
     * NOTE: This MUST be a "Web application" client ID, NOT an "Android" client ID.
     */
    const val GOOGLE_SERVER_CLIENT_ID = "129183375504-h2rv1sn4t5agkr1nl5oieimi48bea2ii.apps.googleusercontent.com"

    /**
     * Android Client ID (Package: com.mel.medicalapp, SHA-1: 23:85:16:EF:69:D0:A3:DD:C3:D8:55:1A:43:C3:9A:DB:AD:D4:02:C0)
     */
    const val GOOGLE_ANDROID_CLIENT_ID = "129183375504-rs2aqbo0omjja8bj5397l2c98nlid53f.apps.googleusercontent.com"

    // ------------------------------------------------------------------------
    // SUPABASE CONFIGURATION
    // ------------------------------------------------------------------------
    const val SUPABASE_URL = "https://ptuqsmureupubeiafihy.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InB0dXFzbXVyZXVwdWJlaWFmaWh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTQ1NDQwMTUsImV4cCI6MjExMDEyMDAxNX0.HnPPwBsOnuxtXAR0q61tYX40WrqSXflPPp4TNffk8Rk"

    const val REST_URL = "$SUPABASE_URL/rest/v1"
    const val AUTH_URL = "$SUPABASE_URL/auth/v1"
    const val STORAGE_URL = "$SUPABASE_URL/storage/v1"

    // ------------------------------------------------------------------------
    // BACKEND API CONFIGURATION (FastAPI)
    // ------------------------------------------------------------------------
    const val BACKEND_URL = "https://mel-medical-app.onrender.com"
    const val BACKEND_URL_FALLBACK = "http://10.0.2.2:8000"
    const val BACKEND_URL_EMULATOR = "http://10.0.2.2:8000"
}
