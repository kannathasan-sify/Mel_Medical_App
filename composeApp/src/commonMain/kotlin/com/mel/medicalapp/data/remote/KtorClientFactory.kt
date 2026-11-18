package com.mel.medicalapp.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createPlatformHttpClient(): HttpClient

object KtorClientFactory {
    fun createJson(): Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun buildClient(): HttpClient {
        return createPlatformHttpClient().config {
            install(ContentNegotiation) {
                json(createJson())
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 120_000L
                connectTimeoutMillis = 90_000L
                socketTimeoutMillis = 120_000L
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
            }
        }
    }
}
