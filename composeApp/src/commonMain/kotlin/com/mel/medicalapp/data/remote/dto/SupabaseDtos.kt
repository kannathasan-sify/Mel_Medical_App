package com.mel.medicalapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PatientProfileDto(
    val id: String,
    val email: String,
    val full_name: String? = null,
    val date_of_birth: String? = null,
    val gender_at_birth: String? = null,
    val age_group: String? = null,
    val identified_groups: List<String> = emptyList(),
    val ranked_values: Map<String, Int> = emptyMap(),
    val primary_challenge: String? = null,
    val medical_summary_detail_level: String = "moderate",
    val mfa_preference: String = "phone_code",
    val preferred_language: String = "en"
)

@Serializable
data class ConsultationDto(
    val id: String? = null,
    val patient_id: String,
    val provider_name: String? = null,
    val visit_type: String = "General Consultation",
    val status: String = "RECORDING",
    val started_at: String? = null,
    val ended_at: String? = null
)

@Serializable
data class TranscriptSegmentDto(
    val id: String? = null,
    val consultation_id: String,
    val sequence_order: Int,
    val start_time_ms: Long,
    val end_time_ms: Long,
    val speaker_role: String = "UNKNOWN",
    val text_content: String,
    val confidence_score: Double = 0.0,
    val is_uncertain: Boolean = false
)

@Serializable
data class VisitSummaryDto(
    val id: String? = null,
    val consultation_id: String,
    val plain_language_overview: String,
    val symptoms_reported: List<String> = emptyList(),
    val provider_explanations: List<String> = emptyList(),
    val diagnoses_explicit: List<String> = emptyList(),
    val medications_discussed: List<String> = emptyList(),
    val treatment_plan: List<String> = emptyList(),
    val unanswered_questions: List<String> = emptyList()
)

@Serializable
data class FollowUpQuestionDto(
    val id: String? = null,
    val visit_summary_id: String,
    val question_text: String,
    val category: String = "GENERAL",
    val is_patient_edited: Boolean = false,
    val is_answered: Boolean = false
)

