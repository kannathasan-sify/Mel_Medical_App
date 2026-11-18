package com.mel.medicalapp.domain.usecase.history

import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.core.security.SecureStorage
import com.mel.medicalapp.data.remote.MelBackendApiClient
import com.mel.medicalapp.data.repository.SupabasePatientRepository
import com.mel.medicalapp.domain.model.ConsultationStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class HistoricalVisitItem(
    val id: String,
    val dateDisplay: String,
    val visitType: String,
    val providerName: String,
    val primaryFocus: String,
    val status: ConsultationStatus,
    val keyFindings: List<String>,
    val pendingQuestionsCount: Int
)

class PatientHistoryService(
    private val repository: SupabasePatientRepository,
    private val secureStorage: SecureStorage? = null,
    private val backendApiClient: MelBackendApiClient? = null
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val STORAGE_KEY = "patient_dynamic_visit_history"

    /**
     * Retrieves the chronological timeline of actual medical visits.
     * Fetches real records from the Supabase backend API and syncs with secure local storage.
     */
    suspend fun getPatientTimeline(patientId: String): List<HistoricalVisitItem> {
        // 1. Try fetching from Supabase via backend API
        try {
            val remoteRes = backendApiClient?.getPatientConsultations(patientId)
            if (remoteRes is NetworkResult.Success && remoteRes.data.isNotEmpty()) {
                val remoteVisits = remoteRes.data.map { item ->
                    val sum = item.visit_summaries.firstOrNull()
                    val findings = if (!sum?.symptoms_reported.isNullOrEmpty()) {
                        sum!!.symptoms_reported.take(3)
                    } else if (!sum?.diagnoses_explicit.isNullOrEmpty()) {
                        sum!!.diagnoses_explicit.take(2)
                    } else {
                        listOf("Consultation synced to Supabase database")
                    }
                    HistoricalVisitItem(
                        id = item.id,
                        dateDisplay = item.created_at?.take(10) ?: "Recent Visit",
                        visitType = item.visit_type ?: "Clinical Consultation",
                        providerName = item.provider_name ?: "Attending Clinician",
                        primaryFocus = sum?.diagnoses_explicit?.firstOrNull() ?: item.visit_type ?: "Consultation Assessment",
                        status = ConsultationStatus.COMPLETED,
                        keyFindings = findings,
                        pendingQuestionsCount = sum?.unanswered_questions?.size ?: 0
                    )
                }
                // Save to local cache
                try {
                    secureStorage?.saveString(STORAGE_KEY, json.encodeToString(remoteVisits))
                } catch (_: Exception) {}
                return remoteVisits
            }
        } catch (_: Exception) {}

        // 2. Fall back to cached local storage
        val storedJson = secureStorage?.getString(STORAGE_KEY)
        if (!storedJson.isNullOrBlank()) {
            try {
                return json.decodeFromString<List<HistoricalVisitItem>>(storedJson)
            } catch (_: Exception) {}
        }
        return emptyList()
    }

    suspend fun saveVisit(visit: HistoricalVisitItem) {
        val currentVisits = getPatientTimeline("patient-id").toMutableList()
        currentVisits.removeAll { it.id == visit.id }
        currentVisits.add(0, visit) // Prepend newest visit
        try {
            secureStorage?.saveString(STORAGE_KEY, json.encodeToString(currentVisits))
        } catch (_: Exception) {}
    }

    suspend fun deleteVisit(visitId: String) {
        // Delete from backend Supabase
        try {
            backendApiClient?.deleteConsultation(visitId)
        } catch (_: Exception) {}

        // Delete from local cache
        val currentVisits = getPatientTimeline("patient-id").filter { it.id != visitId }
        try {
            secureStorage?.saveString(STORAGE_KEY, json.encodeToString(currentVisits))
        } catch (_: Exception) {}
    }
}
