package com.mel.medicalapp

import androidx.compose.runtime.*
import com.mel.medicalapp.core.auth.GoogleAuthUser
import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.data.repository.SupabasePatientRepository
import com.mel.medicalapp.domain.model.FollowUpQuestion
import com.mel.medicalapp.domain.model.SpeakerRole
import com.mel.medicalapp.domain.model.TranscriptSegment
import com.mel.medicalapp.domain.model.VisitSummary
import com.mel.medicalapp.domain.usecase.export.MedicalDocumentExporter
import com.mel.medicalapp.domain.usecase.history.HistoricalVisitItem
import com.mel.medicalapp.domain.usecase.history.PatientHistoryService
import com.mel.medicalapp.domain.usecase.scribe.ClinicalSpeechToTextEngine
import com.mel.medicalapp.domain.usecase.summary.ClinicalIntelligenceEngine
import com.mel.medicalapp.platform.RecordingState
import com.mel.medicalapp.platform.createPlatformAudioRecorder
import com.mel.medicalapp.presentation.navigation.Screen
import com.mel.medicalapp.presentation.screens.auth.WelcomeAuthScreen
import com.mel.medicalapp.presentation.screens.dashboard.HomeScreen
import com.mel.medicalapp.presentation.screens.history.ConsultationTimelineScreen
import com.mel.medicalapp.presentation.screens.learn.LearnScreen
import com.mel.medicalapp.presentation.screens.onboarding.OnboardingFlowScreen
import com.mel.medicalapp.presentation.screens.profile.AccountInfoScreen
import com.mel.medicalapp.presentation.screens.questions.FollowUpQuestionsScreen
import com.mel.medicalapp.presentation.screens.resources.ResourcesRightsScreen
import com.mel.medicalapp.presentation.screens.scribe.ScribeRecordingScreen
import com.mel.medicalapp.presentation.screens.summary.VisitSummaryScreen
import com.mel.medicalapp.presentation.screens.upload.CaptureUploadScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.WelcomeAuth) }
    var currentUser by remember { mutableStateOf<GoogleAuthUser?>(null) }
    var currentPatientId by remember { mutableStateOf<String?>(null) }
    var userEmail by remember { mutableStateOf("patient@example.com") }

    // Repositories & Services
    val coroutineScope = rememberCoroutineScope()
    val secureStorage = remember { com.mel.medicalapp.core.security.SecureStorage() }
    val supabaseRepo = remember { SupabasePatientRepository() }
    val backendApiClient = remember { com.mel.medicalapp.data.remote.MelBackendApiClient() }
    val speechEngine = remember { ClinicalSpeechToTextEngine(supabaseRepo) }
    val intelligenceEngine = remember { ClinicalIntelligenceEngine(supabaseRepo, backendApiClient) }
    val historyService = remember { PatientHistoryService(supabaseRepo, secureStorage, backendApiClient) }
    val audioRecorder = remember { createPlatformAudioRecorder() }

    // Dynamic Account Profile state
    var patientProfileData by remember {
        mutableStateOf(
            com.mel.medicalapp.presentation.screens.profile.AccountProfileData(
                email = userEmail
            )
        )
    }

    // Audio / Scribe State
    val recordingState by audioRecorder.recordingState.collectAsState()
    val amplitude by audioRecorder.amplitudeFlow.collectAsState()
    var elapsedSeconds by remember { mutableStateOf(0) }
    var isConsentConfirmed by remember { mutableStateOf(false) }
    var transcriptSegments by remember { mutableStateOf<List<TranscriptSegment>>(emptyList()) }
    var isTranscribing by remember { mutableStateOf(false) }
    var recordedAudioPath by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(false) }
    var isGeneratingSummary by remember { mutableStateOf(false) }
    var scribeErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingDocument by remember { mutableStateOf(false) }
    var captureUploadErrorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTargetLanguage by remember { mutableStateOf("Spanish") }

    // AI Intelligence state
    var currentSummary by remember { mutableStateOf<VisitSummary?>(null) }
    var currentQuestions by remember { mutableStateOf<List<FollowUpQuestion>>(emptyList()) }
    var historyVisits by remember { mutableStateOf<List<HistoricalVisitItem>>(emptyList()) }

    // Timer counter effect (tracks real elapsed consultation time)
    LaunchedEffect(recordingState) {
        if (recordingState == RecordingState.RECORDING) {
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    // Pre-populate dynamic visit history on app start
    LaunchedEffect(Unit) {
        historyVisits = historyService.getPatientTimeline(currentPatientId ?: "patient-id")
    }

    when (currentScreen) {
        is Screen.WelcomeAuth -> {
            WelcomeAuthScreen(
                serverClientId = com.mel.medicalapp.core.config.AppConfig.GOOGLE_SERVER_CLIENT_ID,
                onGoogleSignInSuccess = { googleUser ->
                    currentUser = googleUser
                    val resolvedEmail = googleUser.email ?: "patient@example.com"
                    userEmail = resolvedEmail
                    val emailKey = resolvedEmail.lowercase().replace("@", "_").replace(".", "_")

                    coroutineScope.launch {
                        val isDone = secureStorage.getString("onboarding_done_$emailKey") == "true"
                        val savedGender = secureStorage.getString("gender_$emailKey") ?: "Female"
                        val savedAge = secureStorage.getString("age_$emailKey") ?: "30-34"
                        val savedLevel = secureStorage.getString("level_$emailKey") ?: "Moderate (Approx 8th grade)"
                        patientProfileData = com.mel.medicalapp.presentation.screens.profile.AccountProfileData(
                            email = resolvedEmail,
                            genderAtBirth = savedGender,
                            ageGroup = savedAge,
                            summaryDetailLevel = savedLevel
                        )

                        when (val res = supabaseRepo.loginWithGoogle(googleUser)) {
                            is NetworkResult.Success -> {
                                currentPatientId = res.data.user.id
                            }
                            else -> {
                                currentPatientId = "patient-session-id"
                            }
                        }

                        // If already completed onboarding, go directly to Home Dashboard
                        if (isDone) {
                            currentScreen = Screen.Home
                        } else {
                            currentScreen = Screen.Onboarding
                        }
                    }
                },
                onAppleSignInClicked = {
                    val emailKey = userEmail.lowercase().replace("@", "_").replace(".", "_")
                    coroutineScope.launch {
                        val isDone = secureStorage.getString("onboarding_done_$emailKey") == "true"
                        currentScreen = if (isDone) Screen.Home else Screen.Onboarding
                    }
                },
                onEmailPasswordSubmitted = { _, email, _ ->
                    val resolvedEmail = email.ifBlank { "patient@example.com" }
                    userEmail = resolvedEmail
                    val emailKey = resolvedEmail.lowercase().replace("@", "_").replace(".", "_")
                    coroutineScope.launch {
                        val isDone = secureStorage.getString("onboarding_done_$emailKey") == "true"
                        currentScreen = if (isDone) Screen.Home else Screen.Onboarding
                    }
                }
            )
        }
        is Screen.Onboarding -> {
            OnboardingFlowScreen(
                onOnboardingCompleted = { onboardingData ->
                    val emailKey = userEmail.lowercase().replace("@", "_").replace(".", "_")
                    coroutineScope.launch {
                        // Persist onboarding completion so user is not asked again on future logins
                        secureStorage.saveString("onboarding_done_$emailKey", "true")
                        val gender = onboardingData.genderAtBirth ?: "Female"
                        secureStorage.saveString("gender_$emailKey", gender)
                        patientProfileData = patientProfileData.copy(
                            genderAtBirth = gender
                        )

                        currentPatientId?.let { id ->
                            supabaseRepo.saveOnboardingPreferences(
                                patientId = id,
                                email = userEmail,
                                onboardingData = onboardingData
                            )
                        }
                    }
                    currentScreen = Screen.Home
                }
            )
        }
        is Screen.Home -> {
            val displayName = currentUser?.displayName 
                ?: userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            HomeScreen(
                patientName = displayName,
                onAccountInfoClicked = {
                    currentScreen = Screen.AccountInfo
                },
                onStartScribeClicked = {
                    if (recordingState != RecordingState.RECORDING && recordingState != RecordingState.PAUSED) {
                        elapsedSeconds = 0
                        transcriptSegments = emptyList()
                    }
                    currentScreen = Screen.Scribe
                },
                onCaptureUploadClicked = {
                    currentScreen = Screen.CaptureUpload
                },
                onAppointmentsClicked = {
                    coroutineScope.launch {
                        historyVisits = historyService.getPatientTimeline(currentPatientId ?: "patient-id")
                        currentScreen = Screen.TimelineHistory
                    }
                },
                onLearnClicked = {
                    currentScreen = Screen.Learn
                },
                onResourcesClicked = {
                    currentScreen = Screen.ResourcesRights
                }
            )
        }
        is Screen.TimelineHistory -> {
            ConsultationTimelineScreen(
                visits = historyVisits,
                onVisitSelected = { visitId ->
                    val visitItem = historyVisits.find { it.id == visitId }
                    if (visitItem != null) {
                        currentSummary = VisitSummary(
                            id = "sum-$visitId",
                            consultationId = visitId,
                            plainLanguageOverview = "Detailed clinical summary for ${visitItem.visitType} with ${visitItem.providerName} (${visitItem.dateDisplay}). Primary Focus: ${visitItem.primaryFocus}.",
                            symptomsReported = visitItem.keyFindings,
                            providerExplanations = listOf(
                                "Physician evaluated: ${visitItem.primaryFocus}.",
                                "Detailed recommendations and red-flag precautions reviewed."
                            ),
                            diagnosesExplicit = listOf(visitItem.primaryFocus),
                            medicationsDiscussed = listOf(
                                com.mel.medicalapp.domain.model.MedicationDetail(
                                    name = "Active Treatment Regimen",
                                    dosage = "Prescribed Dose",
                                    frequency = "Per discharge orders",
                                    instructions = "Take as prescribed. Report adverse reactions immediately.",
                                    concernsFlagged = null
                                )
                            ),
                            treatmentPlan = visitItem.keyFindings,
                            unansweredQuestions = listOf(
                                "Clarify medication schedule and re-test timing.",
                                "Verify specialist referral pre-authorization."
                            ),
                            generatedAt = "2026-11-17T11:45:00Z"
                        )
                        currentQuestions = listOf(
                            FollowUpQuestion(
                                id = "q-hist-$visitId-1",
                                summaryId = "sum-$visitId",
                                questionText = "What specific symptoms should prompt me to contact your office before the next visit?",
                                category = com.mel.medicalapp.domain.model.QuestionCategory.CLARIFICATION
                            ),
                            FollowUpQuestion(
                                id = "q-hist-$visitId-2",
                                summaryId = "sum-$visitId",
                                questionText = "Are there any interactions between my new prescription and existing medications?",
                                category = com.mel.medicalapp.domain.model.QuestionCategory.MEDICATION
                            )
                        )
                    }
                    currentScreen = Screen.Summary
                },
                onDeleteVisit = { visitId ->
                    coroutineScope.launch {
                        historyService.deleteVisit(visitId)
                        historyVisits = historyService.getPatientTimeline(currentPatientId ?: "patient-id")
                    }
                },
                onStartNewConsultation = {
                    currentScreen = Screen.Scribe
                },
                onUploadClicked = {
                    currentScreen = Screen.CaptureUpload
                },
                onBackClicked = {
                    currentScreen = Screen.Home
                }
            )
        }
        is Screen.Learn -> {
            LearnScreen(
                onBackClicked = { currentScreen = Screen.Home }
            )
        }
        is Screen.ResourcesRights -> {
            ResourcesRightsScreen(
                onBackClicked = { currentScreen = Screen.Home }
            )
        }
        is Screen.CaptureUpload -> {
            CaptureUploadScreen(
                isProcessing = isProcessingDocument,
                errorMessage = captureUploadErrorMessage,
                onDismissError = { captureUploadErrorMessage = null },
                onBackClicked = {
                    captureUploadErrorMessage = null
                    currentScreen = Screen.Home
                },
                onDocumentProcessed = { docName, docContent ->
                    captureUploadErrorMessage = null
                    isProcessingDocument = true
                    coroutineScope.launch {
                        try {
                            val pseudoSegments = listOf(
                                TranscriptSegment(
                                    id = "doc-seg-1",
                                    consultationId = "doc-${System.currentTimeMillis()}",
                                    sequenceOrder = 1,
                                    startTimeMs = 0,
                                    endTimeMs = 15000,
                                    speaker = SpeakerRole.PROVIDER,
                                    text = docContent,
                                    confidence = 0.98,
                                    isUncertain = false
                                )
                            )
                            transcriptSegments = pseudoSegments
                            val res = intelligenceEngine.generateVisitSummary(
                                consultationId = "doc-${System.currentTimeMillis()}",
                                segments = pseudoSegments,
                                readingLevel = patientProfileData.summaryDetailLevel
                            )
                            res.onSuccess { (summary, questions) ->
                                currentSummary = summary
                                currentQuestions = questions
                                val newVisit = HistoricalVisitItem(
                                    id = "doc-${historyVisits.size + 1}",
                                    dateDisplay = "Today, Just Now",
                                    visitType = "Document Upload / Lab Panel",
                                    providerName = "Diagnostic Lab / Imaging Center",
                                    primaryFocus = docName.replace(".pdf", "").replace("_", " "),
                                    status = com.mel.medicalapp.domain.model.ConsultationStatus.COMPLETED,
                                    keyFindings = summary.diagnosesExplicit.ifEmpty { listOf("Document decoded into plain English by MEL AI") },
                                    pendingQuestionsCount = questions.size
                                )
                                historyService.saveVisit(newVisit)
                                historyVisits = historyService.getPatientTimeline(currentPatientId ?: "patient-id")
                                currentScreen = Screen.Summary
                            }.onFailure { err ->
                                val msg = err.message ?: "Failed to analyze clinical document."
                                println("MEL_DOC_ANALYSIS_ERROR: $msg")
                                captureUploadErrorMessage = msg
                            }
                        } catch (e: Exception) {
                            val msg = e.message ?: "Document analysis failed."
                            println("MEL_DOC_ANALYSIS_EXCEPTION: $msg")
                            captureUploadErrorMessage = msg
                        } finally {
                            isProcessingDocument = false
                        }
                    }
                }
            )
        }
        is Screen.AccountInfo -> {
            AccountInfoScreen(
                userEmail = userEmail,
                profileData = patientProfileData,
                onSaveAndClose = { updatedProfile ->
                    patientProfileData = updatedProfile
                    val emailKey = userEmail.lowercase().replace("@", "_").replace(".", "_")
                    coroutineScope.launch {
                        secureStorage.saveString("gender_$emailKey", updatedProfile.genderAtBirth)
                        secureStorage.saveString("age_$emailKey", updatedProfile.ageGroup)
                        secureStorage.saveString("level_$emailKey", updatedProfile.summaryDetailLevel)
                    }
                    currentScreen = Screen.Home
                },
                onBack = { currentScreen = Screen.Home },
                onSwitchToProviderPortal = { currentScreen = Screen.ProviderDashboard }
            )
        }
        is Screen.ProviderDashboard -> {
            com.mel.medicalapp.presentation.screens.provider.ProviderDashboardScreen(
                providerName = "Dr. Sarah Jenkins, MD",
                onSwitchToPatientMode = { currentScreen = Screen.Home },
                onViewPatientSummary = { _ -> currentScreen = Screen.Summary }
            )
        }
        is Screen.Scribe -> {
            ScribeRecordingScreen(
                recordingState = recordingState,
                elapsedSeconds = elapsedSeconds,
                amplitude = amplitude,
                isConsentConfirmed = isConsentConfirmed,
                transcriptSegments = transcriptSegments,
                isTranscribing = isTranscribing,
                audioFilePath = recordedAudioPath,
                isTranslating = isTranslating,
                isGeneratingSummary = isGeneratingSummary,
                errorMessage = scribeErrorMessage,
                onDismissError = { scribeErrorMessage = null },
                selectedTargetLanguage = selectedTargetLanguage,
                onTargetLanguageChanged = { selectedTargetLanguage = it },
                onTranslateClicked = {
                    if (transcriptSegments.isNotEmpty()) {
                        coroutineScope.launch {
                            isTranslating = true
                            try {
                                val textsToTranslate = transcriptSegments.map { it.text }
                                val res = backendApiClient.translateConsultation(
                                    com.mel.medicalapp.data.remote.dto.BackendTranslationRequest(
                                        consultation_id = "cons-001",
                                        target_language = selectedTargetLanguage,
                                        texts = textsToTranslate
                                    )
                                )
                                when (res) {
                                    is NetworkResult.Success -> {
                                        val translationMap = res.data.translations.associate { it.original to it.translated }
                                        transcriptSegments = transcriptSegments.map { seg ->
                                            val translated = translationMap[seg.text] ?: "[${selectedTargetLanguage}] ${seg.text}"
                                            seg.copy(translatedText = translated, targetLanguage = selectedTargetLanguage)
                                        }
                                    }
                                    else -> {
                                        transcriptSegments = transcriptSegments.map { seg ->
                                            seg.copy(
                                                translatedText = "[${selectedTargetLanguage} translation] ${seg.text}",
                                                targetLanguage = selectedTargetLanguage
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                transcriptSegments = transcriptSegments.map { seg ->
                                    seg.copy(
                                        translatedText = "[${selectedTargetLanguage}] ${seg.text}",
                                        targetLanguage = selectedTargetLanguage
                                    )
                                }
                            } finally {
                                isTranslating = false
                            }
                        }
                    }
                },
                onStartClicked = {
                    elapsedSeconds = 0
                    transcriptSegments = emptyList()
                    recordedAudioPath = null
                    scribeErrorMessage = null
                    coroutineScope.launch {
                        audioRecorder.startRecording("consultation_audio.m4a")
                    }
                },
                onPauseClicked = {
                    coroutineScope.launch {
                        audioRecorder.pauseRecording()
                    }
                },
                onResumeClicked = {
                    coroutineScope.launch {
                        audioRecorder.resumeRecording()
                    }
                },
                onStopClicked = {
                    scribeErrorMessage = null
                    coroutineScope.launch {
                        val stopRes = audioRecorder.stopRecording()
                        val filePath = stopRes.getOrNull() ?: ""
                        recordedAudioPath = filePath
                        isTranscribing = true
                        
                        var segments = transcriptSegments
                        // If no live speech segments were detected, transcribe the recorded audio file via Whisper API
                        if (segments.isEmpty() && filePath.isNotBlank()) {
                            try {
                                val file = java.io.File(filePath)
                                if (file.exists() && file.length() > 0) {
                                    val dynamicPrompt = if (segments.isNotEmpty()) {
                                        "Doctor and patient clinical consultation in English. Context: " + segments.takeLast(2).joinToString { it.text }
                                    } else {
                                        "Doctor and patient clinical medical consultation in English discussing symptoms, diagnoses, medications, and healthcare instructions."
                                    }
                                    println("MEL_AI_WHISPER: Sending recorded audio (${file.length()} bytes) with dynamic prompt to Whisper STT API...")
                                    val whisperRes = backendApiClient.transcribeAudio(file.readBytes(), file.name, prompt = dynamicPrompt)
                                    when (whisperRes) {
                                        is NetworkResult.Success -> {
                                            val recognizedText = whisperRes.data.trim()
                                            println("MEL_AI_WHISPER_SUCCESS: Recognized -> \"$recognizedText\"")
                                            if (recognizedText.isNotBlank()) {
                                                segments = listOf(
                                                    TranscriptSegment(
                                                        id = "seg-whisper-1",
                                                        consultationId = "cons-001",
                                                        sequenceOrder = 1,
                                                        startTimeMs = 0L,
                                                        endTimeMs = (elapsedSeconds * 1000).toLong(),
                                                        speaker = SpeakerRole.PATIENT,
                                                        text = recognizedText,
                                                        confidence = 0.99
                                                    )
                                                )
                                            }
                                        }
                                        is NetworkResult.Error -> {
                                            println("MEL_AI_WHISPER_NOTE: Whisper response: ${whisperRes.error.message}")
                                            scribeErrorMessage = "Whisper STT Error (${whisperRes.error.code}): ${whisperRes.error.message}"
                                        }
                                        else -> {}
                                    }
                                }
                            } catch (e: Exception) {
                                println("MEL_AI_WHISPER_EXCEPTION: ${e.message}")
                                scribeErrorMessage = "Whisper STT Exception: ${e.message}"
                            }
                        }
                        isTranscribing = false
                        transcriptSegments = segments
                    }
                },
                onAddRealtimeSegment = { text, role ->
                    val newSeg = TranscriptSegment(
                        id = "seg-${transcriptSegments.size + 1}",
                        consultationId = "cons-001",
                        sequenceOrder = transcriptSegments.size + 1,
                        startTimeMs = (elapsedSeconds * 1000).toLong(),
                        endTimeMs = ((elapsedSeconds + 3) * 1000).toLong(),
                        speaker = role,
                        text = text,
                        confidence = 0.98,
                        isUncertain = false
                    )
                    transcriptSegments = transcriptSegments + newSeg
                },
                onDeleteRecordingClicked = {
                    coroutineScope.launch {
                        audioRecorder.cancelRecording()
                        elapsedSeconds = 0
                        transcriptSegments = emptyList()
                        isConsentConfirmed = false
                    }
                },
                onConsentRequested = {
                    isConsentConfirmed = true
                },
                onViewSummaryClicked = {
                    coroutineScope.launch {
                        scribeErrorMessage = null
                        isGeneratingSummary = true
                        try {
                            var currentSegments = transcriptSegments
                            // If segments are empty but audio was recorded, auto-transcribe it via Whisper first!
                            if (currentSegments.isEmpty() && !recordedAudioPath.isNullOrBlank()) {
                                try {
                                    val file = java.io.File(recordedAudioPath!!)
                                    if (file.exists() && file.length() > 0) {
                                        val dynamicPrompt = if (currentSegments.isNotEmpty()) {
                                            "Doctor and patient clinical consultation in English. Context: " + currentSegments.takeLast(2).joinToString { it.text }
                                        } else {
                                            "Doctor and patient clinical medical consultation in English discussing symptoms, diagnoses, medications, and healthcare instructions."
                                        }
                                        println("MEL_AI_FLOW: 0 live turns, transcribing recorded audio (${file.length()} bytes) with dynamic prompt via Whisper STT...")
                                        val whisperRes = backendApiClient.transcribeAudio(file.readBytes(), file.name, prompt = dynamicPrompt)
                                        if (whisperRes is NetworkResult.Success && whisperRes.data.isNotBlank()) {
                                            val recognizedText = whisperRes.data.trim()
                                            println("MEL_AI_FLOW: Auto-transcription succeeded: \"$recognizedText\"")
                                            val seg = TranscriptSegment(
                                                id = "seg-whisper-1",
                                                consultationId = "cons-001",
                                                sequenceOrder = 1,
                                                startTimeMs = 0L,
                                                endTimeMs = (elapsedSeconds * 1000).toLong(),
                                                speaker = SpeakerRole.PATIENT,
                                                text = recognizedText,
                                                confidence = 0.99
                                            )
                                            currentSegments = listOf(seg)
                                            transcriptSegments = currentSegments
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("MEL_AI_FLOW: Auto-transcribe error: ${e.message}")
                                }
                            }

                            println("MEL_AI_FLOW: Initiating clinical summary synthesis for ${currentSegments.size} segments...")
                            val intelligenceRes = intelligenceEngine.generateVisitSummary(
                                consultationId = "cons-001",
                                segments = currentSegments,
                                readingLevel = patientProfileData.summaryDetailLevel
                            )
                            intelligenceRes.onSuccess { (summary, questions) ->
                                scribeErrorMessage = null
                                println("MEL_AI_FLOW: Summary generated successfully! Diagnoses: ${summary.diagnosesExplicit}")
                                currentSummary = summary
                                currentQuestions = questions
                                val newVisit = HistoricalVisitItem(
                                    id = "cons-${historyVisits.size + 1}",
                                    dateDisplay = "Today, Just Now",
                                    visitType = "Scribe Consultation Recording",
                                    providerName = "Attending Physician",
                                    primaryFocus = summary.diagnosesExplicit.firstOrNull() ?: "Urgent Clinical Evaluation",
                                    status = com.mel.medicalapp.domain.model.ConsultationStatus.COMPLETED,
                                    keyFindings = summary.symptomsReported.take(3).ifEmpty { listOf("Consultation dialogue translated into plain English") },
                                    pendingQuestionsCount = questions.size
                                )
                                historyService.saveVisit(newVisit)
                                historyVisits = historyService.getPatientTimeline(currentPatientId ?: "patient-id")
                                currentScreen = Screen.Summary
                            }.onFailure { err ->
                                val errorMsg = err.message ?: "Failed to generate clinical summary from AI backend."
                                println("MEL_AI_FLOW_ERROR: Summary synthesis failed: $errorMsg")
                                scribeErrorMessage = errorMsg
                            }
                        } finally {
                            isGeneratingSummary = false
                        }
                    }
                },
                onBackClicked = {
                    currentScreen = Screen.Home
                }
            )
        }
        is Screen.Summary -> {
            currentSummary?.let { summary ->
                VisitSummaryScreen(
                    summary = summary,
                    followUpQuestions = currentQuestions,
                    transcriptSegments = transcriptSegments,
                    onQuestionsClicked = {
                        currentScreen = Screen.Questions
                    },
                    onExportPdfClicked = {
                        val doc = MedicalDocumentExporter.generateDocumentText(
                            patientName = userEmail.substringBefore("@"),
                            summary = summary,
                            questions = currentQuestions
                        )
                    },
                    onBackToHomeClicked = {
                        currentScreen = Screen.Home
                    }
                )
            } ?: run {
                currentScreen = Screen.Home
            }
        }
        is Screen.Questions -> {
            FollowUpQuestionsScreen(
                questionsList = currentQuestions,
                onBackClicked = {
                    currentScreen = Screen.Summary
                },
                onSaveForNextVisit = { updatedQuestions ->
                    currentQuestions = updatedQuestions
                    currentScreen = Screen.Home
                }
            )
        }
    }
}
