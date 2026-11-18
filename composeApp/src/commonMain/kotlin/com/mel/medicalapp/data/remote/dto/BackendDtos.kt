package com.mel.medicalapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BackendSynthesisRequest(
    val consultation_id: String,
    val patient_id: String,
    val transcript_text: String,
    val reading_level: String = "moderate",
    val patient_historical_notes: String? = null
)

@Serializable
data class BackendMedicationDetail(
    val name: String,
    val dosage: String? = null,
    val frequency: String? = null,
    val instructions: String? = null,
    val concerns_flagged: String? = null
)

@Serializable
data class BackendVisitSummaryResponse(
    val consultation_id: String,
    val plain_language_overview: String,
    val symptoms_reported: List<String> = emptyList(),
    val provider_explanations: List<String> = emptyList(),
    val diagnoses_explicit: List<String> = emptyList(),
    val medications_discussed: List<BackendMedicationDetail> = emptyList(),
    val treatment_plan: List<String> = emptyList(),
    val unanswered_questions: List<String> = emptyList(),
    val follow_up_questions: List<String> = emptyList(),
    val emergency_flag: Boolean = false,
    val emergency_notice: String? = null
)

@Serializable
data class BackendTranslationRequest(
    val consultation_id: String? = null,
    val target_language: String = "Spanish",
    val texts: List<String> = emptyList()
)

@Serializable
data class BackendTranslationItem(
    val original: String,
    val translated: String
)

@Serializable
data class BackendTranslationResponse(
    val target_language: String,
    val translations: List<BackendTranslationItem> = emptyList()
)

@Serializable
data class BackendExplainTermRequest(
    val term: String
)

@Serializable
data class BackendExplainTermResponse(
    val term: String,
    val pronunciation: String = "",
    val plainMeaning: String = "",
    val whyItMatters: String = "",
    val doctorQuestions: List<String> = emptyList(),
    val commonSymptomsOrUses: String? = null,
    val category: String = "General Health"
)

@Serializable
data class BackendTranscriptionResponse(
    val text: String = "",
    val status: String = ""
)

@Serializable
data class BackendConsultationItem(
    val id: String,
    val patient_id: String? = null,
    val provider_name: String? = null,
    val visit_type: String? = null,
    val status: String? = null,
    val started_at: String? = null,
    val created_at: String? = null,
    val visit_summaries: List<BackendVisitSummaryDetail> = emptyList()
)

@Serializable
data class BackendVisitSummaryDetail(
    val id: String? = null,
    val plain_language_overview: String? = null,
    val symptoms_reported: List<String> = emptyList(),
    val diagnoses_explicit: List<String> = emptyList(),
    val treatment_plan: List<String> = emptyList(),
    val unanswered_questions: List<String> = emptyList()
)

@Serializable
data class BackendPatientConsultationsResponse(
    val patient_id: String,
    val consultations: List<BackendConsultationItem> = emptyList()
)


