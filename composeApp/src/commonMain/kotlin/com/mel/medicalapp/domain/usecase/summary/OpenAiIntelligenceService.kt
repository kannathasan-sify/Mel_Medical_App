package com.mel.medicalapp.domain.usecase.summary

import com.mel.medicalapp.core.config.SupabaseConfig
import com.mel.medicalapp.core.result.AppError
import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.data.remote.KtorClientFactory
import com.mel.medicalapp.domain.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OpenAiChatRequest(
    val model: String = "gpt-4o",
    val messages: List<OpenAiMessage>,
    val temperature: Double = 0.2
)

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenAiChatResponse(
    val choices: List<OpenAiChoice>
)

@Serializable
data class OpenAiChoice(
    val message: OpenAiMessage
)

@Serializable
data class SynthesizedClinicalOutput(
    val plainLanguageOverview: String,
    val diagnosesExplicit: List<String>,
    val treatmentPlan: List<String>,
    val medicationsDiscussed: List<MedicationDetail>,
    val unansweredQuestions: List<String>,
    val followUpQuestions: List<String>
)

class OpenAiIntelligenceService(
    private val httpClient: HttpClient = KtorClientFactory.buildClient(),
    private val openAiApiKey: String? = null // Passed or fetched securely from backend/environment
) {
    /**
     * Synthesizes visit summary & follow-up questions using OpenAI API (GPT-4o).
     * Enforces strict prompt engineering: no hallucination, clinical terminology preservation,
     * plain-language translation, and neutral patient empowerment tone.
     */
    suspend fun generateClinicalSummaryWithOpenAi(
        consultationId: String,
        transcriptText: String,
        readingLevel: String = "moderate"
    ): Result<Pair<VisitSummary, List<FollowUpQuestion>>> {
        val apiKey = openAiApiKey?.takeIf { it.isNotBlank() }
        
        // If no direct API key is set or running in standalone mode, fall back to certified local clinical pipeline
        if (apiKey == null) {
            return Result.failure(Exception("OPENAI_API_KEY_NOT_CONFIGURED"))
        }

        return try {
            val systemPrompt = """
                You are MEL (Medical Empowerment Liaison), a clinical communication assistant.
                Your task is to analyze the provided consultation transcript between a Patient and a Healthcare Provider.
                
                Rules:
                1. Adapt language to reading comprehension level: $readingLevel (simple = 5th grade, moderate = 8th grade, complex = 12th grade).
                2. Do not invent diagnoses or treatments. Only state what was explicitly discussed.
                3. Preserve clinical terms alongside their plain explanations.
                4. Flag any medication difficulties (e.g. inability to swallow pills/dysphagia).
                5. Identify unanswered questions or gaps in the conversation.
                6. Generate 4 respectful follow-up questions the patient can ask their doctor.
                
                Respond ONLY with a valid JSON object matching this schema:
                {
                  "plainLanguageOverview": "string",
                  "diagnosesExplicit": ["string"],
                  "treatmentPlan": ["string"],
                  "medicationsDiscussed": [{"name": "string", "dosage": "string", "frequency": "string", "instructions": "string", "concernsFlagged": "string"}],
                  "unansweredQuestions": ["string"],
                  "followUpQuestions": ["string"]
                }
            """.trimIndent()

            val request = OpenAiChatRequest(
                model = "gpt-4o",
                messages = listOf(
                    OpenAiMessage("system", systemPrompt),
                    OpenAiMessage("user", "Transcript:\n$transcriptText")
                )
            )

            val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val chatRes = response.body<OpenAiChatResponse>()
                val rawContent = chatRes.choices.firstOrNull()?.message?.content ?: "{}"
                val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }
                val parsed = jsonParser.decodeFromString<SynthesizedClinicalOutput>(rawContent)

                val summary = VisitSummary(
                    id = "sum-$consultationId",
                    consultationId = consultationId,
                    plainLanguageOverview = parsed.plainLanguageOverview,
                    symptomsReported = emptyList(),
                    providerExplanations = emptyList(),
                    diagnosesExplicit = parsed.diagnosesExplicit,
                    medicationsDiscussed = parsed.medicationsDiscussed,
                    treatmentPlan = parsed.treatmentPlan,
                    unansweredQuestions = parsed.unansweredQuestions,
                    generatedAt = "2026-11-13T12:00:00Z"
                )

                val questions = parsed.followUpQuestions.mapIndexed { idx, qText ->
                    FollowUpQuestion(
                        id = "q-ai-${idx + 1}",
                        summaryId = summary.id,
                        questionText = qText,
                        category = QuestionCategory.CLARIFICATION
                    )
                }

                Result.success(Pair(summary, questions))
            } else {
                val errBody = response.bodyAsText()
                Result.failure(Exception("OpenAI API failed: $errBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

