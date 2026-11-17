package com.mel.medicalapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Consultation(
    val id: String,
    val patientId: String,
    val providerId: String? = null,
    val status: ConsultationStatus,
    val startedAt: String,
    val endedAt: String? = null,
    val visitType: String = "General Checkup"
)

enum class ConsultationStatus {
    SCHEDULED,
    RECORDING,
    PAUSED,
    PROCESSING,
    COMPLETED,
    CANCELLED
}

@Serializable
data class TranscriptSegment(
    val id: String,
    val consultationId: String,
    val sequenceOrder: Int,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val speaker: SpeakerRole,
    val text: String,
    val confidence: Double,
    val isUncertain: Boolean = false
)

enum class SpeakerRole {
    PATIENT,
    PROVIDER,
    UNKNOWN
}

@Serializable
data class VisitSummary(
    val id: String,
    val consultationId: String,
    val plainLanguageOverview: String,
    val symptomsReported: List<String>,
    val providerExplanations: List<String>,
    val diagnosesExplicit: List<String>,
    val medicationsDiscussed: List<MedicationDetail>,
    val treatmentPlan: List<String>,
    val unansweredQuestions: List<String>,
    val generatedAt: String
)

@Serializable
data class MedicationDetail(
    val name: String,
    val dosage: String? = null,
    val frequency: String? = null,
    val instructions: String? = null,
    val concernsFlagged: String? = null
)

@Serializable
data class FollowUpQuestion(
    val id: String,
    val summaryId: String,
    val questionText: String,
    val category: QuestionCategory,
    val isPatientEdited: Boolean = false,
    val isAnswered: Boolean = false
)

enum class QuestionCategory {
    CLARIFICATION,
    MEDICATION,
    NEXT_STEPS,
    HISTORY,
    GENERAL
}

