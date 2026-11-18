package com.mel.medicalapp.data.remote

import com.mel.medicalapp.core.config.AppConfig
import com.mel.medicalapp.core.result.AppError
import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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
            val response = httpClient.post("$baseUrl/api/v1/consultations/synthesize") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val data = response.body<BackendVisitSummaryResponse>()
                NetworkResult.Success(data)
            } else {
                val err = response.bodyAsText()
                NetworkResult.Error(AppError.serverError("Backend synthesis failed (${response.status}): $err"))
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown error"
            val friendly = if (msg.contains("timeout", ignoreCase = true)) {
                "Connection timed out contacting Clinical AI backend. The cloud server may have been waking up. Please try again."
            } else {
                "Failed to connect to MEL backend service: $msg"
            }
            NetworkResult.Error(AppError.networkError(friendly))
        }
    }

    /**
     * Translates spoken dialogue turns into a target language using AI.
     */
    suspend fun translateConsultation(
        request: BackendTranslationRequest
    ): NetworkResult<BackendTranslationResponse> {
        return try {
            val response = httpClient.post("$baseUrl/api/v1/consultations/translate") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val data = response.body<BackendTranslationResponse>()
                NetworkResult.Success(data)
            } else {
                val err = response.bodyAsText()
                NetworkResult.Error(AppError.serverError("Translation failed (${response.status}): $err"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError("Failed to connect to MEL translation service: ${e.message}"))
        }
    }

    /**
     * Retrieves an AI-powered plain-language medical breakdown of any term, drug, or test.
     */
    suspend fun explainMedicalTerm(
        term: String
    ): NetworkResult<BackendExplainTermResponse> {
        return try {
            val request = BackendExplainTermRequest(term = term)
            val response = httpClient.post("$baseUrl/api/v1/consultations/explain-term") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val data = response.body<BackendExplainTermResponse>()
                NetworkResult.Success(data)
            } else {
                val err = response.bodyAsText()
                NetworkResult.Error(AppError.serverError("Term lookup failed (${response.status}): $err"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError("Failed to connect to MEL learning service: ${e.message}"))
        }
    }

    /**
     * Transcribes actual recorded audio bytes using OpenAI Whisper via the backend API.
     */
    suspend fun transcribeAudio(
        audioBytes: ByteArray,
        filename: String = "recording.m4a",
        prompt: String? = null
    ): NetworkResult<String> {
        var lastException: Exception? = null
        for (attempt in 1..2) {
            try {
                val response: HttpResponse = httpClient.submitFormWithBinaryData(
                    url = "$baseUrl/api/v1/consultations/transcribe",
                    formData = formData {
                        append("file", audioBytes, Headers.build {
                            append(HttpHeaders.ContentType, "audio/m4a")
                            append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                        })
                        append("language", "en")
                        if (!prompt.isNullOrBlank()) {
                            append("prompt", prompt)
                        }
                    }
                )
                if (response.status.isSuccess()) {
                    val data = response.body<BackendTranscriptionResponse>()
                    println("MEL_BACKEND_WHISPER: Received transcription: \"${data.text}\"")
                    return NetworkResult.Success(data.text)
                } else {
                    val err = response.bodyAsText()
                    println("MEL_BACKEND_WHISPER: Transcription error response: $err")
                    return NetworkResult.Error(AppError.serverError("Transcription failed: ${response.status} - $err"))
                }
            } catch (e: Exception) {
                lastException = e
                println("MEL_BACKEND_WHISPER: Attempt $attempt failed (${e.message}). Retrying...")
            }
        }
        val msg = lastException?.message ?: "Unknown network error"
        val friendlyMsg = if (msg.contains("timeout", ignoreCase = true)) {
            "Connection timed out. The Render cloud server may have been sleeping (cold start). The server is now awake—please tap 'End & Transcribe' to retry."
        } else {
            "Transcription network error: $msg"
        }
        return NetworkResult.Error(AppError.networkError(friendlyMsg))
    }

    /**
     * Retrieves historical visits and consultations stored in Supabase via the backend API.
     */
    suspend fun getPatientConsultations(
        patientId: String
    ): NetworkResult<List<BackendConsultationItem>> {
        return try {
            val response = httpClient.get("$baseUrl/api/v1/consultations/patient/$patientId")

            if (response.status.isSuccess()) {
                val data = response.body<BackendPatientConsultationsResponse>()
                NetworkResult.Success(data.consultations)
            } else {
                val err = response.bodyAsText()
                NetworkResult.Error(AppError.serverError("Failed to fetch consultations ($err)"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError("Could not retrieve patient visits: ${e.message}"))
        }
    }

    /**
     * Deletes a consultation and its associated records from Supabase via the backend API.
     */
    suspend fun deleteConsultation(
        consultationId: String
    ): NetworkResult<Boolean> {
        return try {
            val response = httpClient.delete("$baseUrl/api/v1/consultations/$consultationId")

            if (response.status.isSuccess()) {
                NetworkResult.Success(true)
            } else {
                NetworkResult.Error(AppError.serverError("Failed to delete consultation (${response.status})"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError("Failed to delete consultation: ${e.message}"))
        }
    }
}
