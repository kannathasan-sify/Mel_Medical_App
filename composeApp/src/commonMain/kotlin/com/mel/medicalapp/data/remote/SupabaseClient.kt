package com.mel.medicalapp.data.remote

import com.mel.medicalapp.core.auth.GoogleAuthUser
import com.mel.medicalapp.core.config.SupabaseConfig
import com.mel.medicalapp.core.result.AppError
import com.mel.medicalapp.core.result.NetworkResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseAuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val user: SupabaseUser
)

@Serializable
data class SupabaseUser(
    val id: String,
    val email: String? = null,
    val created_at: String? = null
)

class SupabaseClient(
    val httpClient: HttpClient = KtorClientFactory.buildClient(),
    val supabaseUrl: String = SupabaseConfig.SUPABASE_URL,
    val anonKey: String = SupabaseConfig.SUPABASE_ANON_KEY
) {
    var userAccessToken: String? = null

    fun HttpRequestBuilder.applyAuthHeaders() {
        header("apikey", anonKey)
        val token = userAccessToken ?: anonKey
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
    }

    // ---------------------------------------------------------
    // AUTHENTICATION
    // ---------------------------------------------------------

    suspend fun signInWithGoogleIdToken(googleUser: GoogleAuthUser): NetworkResult<SupabaseAuthResponse> {
        return try {
            val response = httpClient.post("$supabaseUrl/auth/v1/token?grant_type=id_token") {
                applyAuthHeaders()
                setBody(
                    mapOf(
                        "provider" to "google",
                        "id_token" to googleUser.idToken
                    )
                )
            }
            if (response.status.isSuccess()) {
                val authRes = response.body<SupabaseAuthResponse>()
                userAccessToken = authRes.access_token
                NetworkResult.Success(authRes)
            } else {
                val errorBody = response.bodyAsText()
                NetworkResult.Error(AppError.authError("Google Auth token exchange failed: $errorBody"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError(e.message ?: "Authentication failed"))
        }
    }

    // ---------------------------------------------------------
    // POSTGREST TABLE CRUD
    // ---------------------------------------------------------

    suspend inline fun <reified T> queryTable(
        tableName: String,
        queryParams: String = "select=*"
    ): NetworkResult<T> {
        return try {
            val url = "$supabaseUrl/rest/v1/$tableName?$queryParams"
            val response = httpClient.get(url) {
                applyAuthHeaders()
            }
            if (response.status.isSuccess()) {
                NetworkResult.Success(response.body<T>())
            } else {
                NetworkResult.Error(AppError.serverError("Supabase query error: ${response.status}"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError(e.message ?: "Query failed"))
        }
    }

    suspend inline fun <reified Req, reified Res> insertRecord(
        tableName: String,
        record: Req,
        upsert: Boolean = false
    ): NetworkResult<Res> {
        return try {
            val url = "$supabaseUrl/rest/v1/$tableName"
            val response = httpClient.post(url) {
                applyAuthHeaders()
                if (upsert) {
                    header("Prefer", "resolution=merge-duplicates,return=representation")
                } else {
                    header("Prefer", "return=representation")
                }
                setBody(record)
            }
            if (response.status.isSuccess()) {
                NetworkResult.Success(response.body<Res>())
            } else {
                val errorBody = response.bodyAsText()
                NetworkResult.Error(AppError.serverError("Failed to insert record: $errorBody"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError(e.message ?: "Insert failed"))
        }
    }

    suspend inline fun <reified Req> updateRecord(
        tableName: String,
        filterQuery: String,
        body: Req
    ): NetworkResult<Unit> {
        return try {
            val url = "$supabaseUrl/rest/v1/$tableName?$filterQuery"
            val response = httpClient.patch(url) {
                applyAuthHeaders()
                setBody(body)
            }
            if (response.status.isSuccess()) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(AppError.serverError("Failed to update: ${response.status}"))
            }
        } catch (e: Exception) {
            NetworkResult.Error(AppError.networkError(e.message ?: "Update failed"))
        }
    }
}
