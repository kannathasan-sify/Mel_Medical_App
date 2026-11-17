package com.mel.medicalapp.core.result

import kotlinx.serialization.Serializable

sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Error(val error: AppError) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>
}

@Serializable
data class AppError(
    val code: String,
    val message: String,
    val details: String? = null
) {
    companion object {
        fun networkError(msg: String = "Network connection failed. Please check your internet.") =
            AppError("ERR_NETWORK", msg)
            
        fun authError(msg: String = "Authentication failed. Please verify your credentials.") =
            AppError("ERR_AUTH", msg)

        fun serverError(msg: String = "An error occurred on our secure servers.") =
            AppError("ERR_SERVER", msg)

        fun unknownError(msg: String = "An unexpected error occurred.") =
            AppError("ERR_UNKNOWN", msg)
    }
}

