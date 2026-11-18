package com.mel.medicalapp.data.remote

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import java.time.Duration

actual fun createPlatformHttpClient(): HttpClient = HttpClient(OkHttp) {
    engine {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(Duration.ofSeconds(120))
            readTimeout(Duration.ofSeconds(120))
            writeTimeout(Duration.ofSeconds(120))
        }
    }
}
