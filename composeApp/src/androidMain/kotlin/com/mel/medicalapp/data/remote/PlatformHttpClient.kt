package com.mel.medicalapp.data.remote

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual fun createPlatformHttpClient(): HttpClient = HttpClient(OkHttp) {
    engine {
        config {
            retryOnConnectionFailure(true)
        }
    }
}

