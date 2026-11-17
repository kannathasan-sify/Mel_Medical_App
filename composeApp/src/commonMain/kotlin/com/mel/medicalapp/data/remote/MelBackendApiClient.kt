package com.mel.medicalapp.data.remote

import com.mel.medicalapp.core.config.AppConfig
import com.mel.medicalapp.core.result.AppError
import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.data.remote.dto.BackendSynthesisRequest
import com.mel.medicalapp.data.remote.dto.BackendVisitSummaryResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class MelBackendApiClient(
    private val httpClient: HttpClient = KtorClientFactory.buildClient(),
    private val baseUrl: String = AppConfig.BACKEND_URL
) {
    /**
     * Sends consultation transcripts to the FastAPI backend at POST /api/v1/consultations/synthesize
     */
    suspend fun synthesizeConsultation(
        request: BackendSynthesisRequest
    ): NetworkResult<BackendVisitSummaryResponse> {
        return try {
            val response = try {
                httpClient.post("$baseUrl/api/v1/consultations/synthesize") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
            } catch (e: Exception) {
                // If 10.0.2.2 fails (e.g. running on desktop or iOS), try localhost fallback
                httpClient.post("${AppConfig.BACKEND_URL_FALLBACK}/api/v1/consultations/synthesize") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }
            }

            if (response.status.isSuccess()) {
                val data = response.body<BackendVisitSummaryResponse>()
                NetworkResult.Success(data)
            } else {
                val err = response.bodyAsText()
                NetworkResult.Error(AppError.serverError("Backend synthesis failed (${response.status}): $err"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError("Failed to connect to MEL backend service: ${e.message}"))
        }
    }
}

