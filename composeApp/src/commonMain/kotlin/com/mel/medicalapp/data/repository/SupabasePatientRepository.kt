package com.mel.medicalapp.data.repository

import com.mel.medicalapp.core.auth.GoogleAuthUser
import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.data.remote.SupabaseAuthResponse
import com.mel.medicalapp.data.remote.SupabaseClient
import com.mel.medicalapp.data.remote.dto.*
import com.mel.medicalapp.presentation.screens.onboarding.OnboardingData

class SupabasePatientRepository(
    private val supabaseClient: SupabaseClient = SupabaseClient()
) {
    suspend fun loginWithGoogle(user: GoogleAuthUser): NetworkResult<SupabaseAuthResponse> {
        return supabaseClient.signInWithGoogleIdToken(user)
    }

    suspend fun getPatientProfile(patientId: String): NetworkResult<List<PatientProfileDto>> {
        return supabaseClient.queryTable(
            tableName = "patient_profiles",
            queryParams = "id=eq.$patientId&select=*"
        )
    }

    suspend fun saveOnboardingPreferences(
        patientId: String,
        email: String,
        onboardingData: OnboardingData
    ): NetworkResult<List<PatientProfileDto>> {
        val dto = PatientProfileDto(
            id = patientId,
            email = email,
            gender_at_birth = onboardingData.genderAtBirth,
            identified_groups = onboardingData.identityGroups.toList(),
            ranked_values = onboardingData.rankedValues,
            primary_challenge = onboardingData.primaryChallenge
        )
        return supabaseClient.insertRecord(
            tableName = "patient_profiles",
            record = listOf(dto),
            upsert = true
        )
    }

    suspend fun createConsultation(consultation: ConsultationDto): NetworkResult<List<ConsultationDto>> {
        return supabaseClient.insertRecord(
            tableName = "consultations",
            record = listOf(consultation)
        )
    }

    suspend fun getConsultations(patientId: String): NetworkResult<List<ConsultationDto>> {
        return supabaseClient.queryTable(
            tableName = "consultations",
            queryParams = "patient_id=eq.$patientId&order=started_at.desc"
        )
    }

    suspend fun insertTranscriptSegments(segments: List<TranscriptSegmentDto>): NetworkResult<List<TranscriptSegmentDto>> {
        return supabaseClient.insertRecord(
            tableName = "transcript_segments",
            record = segments
        )
    }

    suspend fun getVisitSummary(consultationId: String): NetworkResult<List<VisitSummaryDto>> {
        return supabaseClient.queryTable(
            tableName = "visit_summaries",
            queryParams = "consultation_id=eq.$consultationId&select=*"
        )
    }

    suspend fun getFollowUpQuestions(summaryId: String): NetworkResult<List<FollowUpQuestionDto>> {
        return supabaseClient.queryTable(
            tableName = "follow_up_questions",
            queryParams = "visit_summary_id=eq.$summaryId&select=*"
        )
    }
}

