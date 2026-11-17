package com.mel.medicalapp.domain.usecase.history

import com.mel.medicalapp.data.remote.dto.ConsultationDto
import com.mel.medicalapp.data.repository.SupabasePatientRepository
import com.mel.medicalapp.domain.model.Consultation
import com.mel.medicalapp.domain.model.ConsultationStatus
import kotlinx.coroutines.delay

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
    private val repository: SupabasePatientRepository
) {
    /**
     * Retrieves the chronological timeline of medical visits.
     * Incorporates both current encounters and historical appointments (e.g. Test Scenario 01 & 02).
     */
    suspend fun getPatientTimeline(patientId: String): List<HistoricalVisitItem> {
        delay(600) // Simulates retrieval & sync from Supabase

        return listOf(
            HistoricalVisitItem(
                id = "cons-001",
                dateDisplay = "Today, 11:30 AM",
                visitType = "Urgent Outpatient Evaluation",
                providerName = "Dr. Sarah Jenkins, MD",
                primaryFocus = "Untreated UTI, Flank Pain & Post-MVA Injuries",
                status = ConsultationStatus.COMPLETED,
                keyFindings = listOf(
                    "Suspected ascending pyelonephritis",
                    "Immediate ED referral for IV antibiotics",
                    "Reducible post-accident umbilical hernia",
                    "ENT referral for post-accident hearing changes"
                ),
                pendingQuestionsCount = 4
            ),
            HistoricalVisitItem(
                id = "cons-000",
                dateDisplay = "2 Weeks Ago",
                visitType = "Emergency Department Visit",
                providerName = "Memorial Hospital ER",
                primaryFocus = "Initial UTI Diagnosis following rear-end collision",
                status = ConsultationStatus.COMPLETED,
                keyFindings = listOf(
                    "Macrobid prescribed (untaken due to dysphagia)",
                    "Minor neck strain documented",
                    "No abdominal imaging performed at that time"
                ),
                pendingQuestionsCount = 2
            ),
            HistoricalVisitItem(
                id = "cons-prior-1",
                dateDisplay = "6 Months Ago",
                visitType = "Cardiology & Chronic Care Follow-up",
                providerName = "Dr. Robert Chen, Cardiologist",
                primaryFocus = "Heart Palpitations, Lightheadedness & Diabetes",
                status = ConsultationStatus.COMPLETED,
                keyFindings = listOf(
                    "Irregular heartbeat documented via EKG",
                    "Sleep apnea study referral initiated",
                    "Medication cost assistance programs discussed"
                ),
                pendingQuestionsCount = 1
            )
        )
    }
}

