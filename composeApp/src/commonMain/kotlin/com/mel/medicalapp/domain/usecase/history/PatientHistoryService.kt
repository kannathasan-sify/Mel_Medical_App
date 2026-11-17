package com.mel.medicalapp.domain.usecase.history

import com.mel.medicalapp.core.security.SecureStorage
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
    private val secureStorage: SecureStorage? = null
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val STORAGE_KEY = "patient_dynamic_visit_history"

    /**
     * Retrieves the chronological timeline of actual medical visits.
     * Contains only dynamic user-recorded consultations or documents (no hardcoded static visits).
     */
    suspend fun getPatientTimeline(patientId: String): List<HistoricalVisitItem> {
        val storedJson = secureStorage?.getString(STORAGE_KEY)
        if (!storedJson.isNullOrBlank()) {
            try {
                return json.decodeFromString<List<HistoricalVisitItem>>(storedJson)
            } catch (_: Exception) {}
        }
        // Starts completely empty until the patient records a visit or uploads documents
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
        val currentVisits = getPatientTimeline("patient-id").filter { it.id != visitId }
        try {
            secureStorage?.saveString(STORAGE_KEY, json.encodeToString(currentVisits))
        } catch (_: Exception) {}
    }
}
