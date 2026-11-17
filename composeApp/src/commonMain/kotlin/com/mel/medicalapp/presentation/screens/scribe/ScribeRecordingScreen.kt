package com.mel.medicalapp.presentation.screens.scribe

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.domain.model.SpeakerRole
import com.mel.medicalapp.domain.model.TranscriptSegment
import com.mel.medicalapp.platform.RecordingState
import com.mel.medicalapp.platform.rememberPlatformSpeechRecognizer
import com.mel.medicalapp.presentation.components.AmbientRecordingHalo
import com.mel.medicalapp.presentation.components.AudioWaveformVisualizer
import com.mel.medicalapp.presentation.components.EmergencySafetyAnchor
import com.mel.medicalapp.presentation.theme.MelColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScribeRecordingScreen(
    recordingState: RecordingState,
    elapsedSeconds: Int,
    amplitude: Float,
    isConsentConfirmed: Boolean,
    transcriptSegments: List<TranscriptSegment> = emptyList(),
    isTranscribing: Boolean = false,
    onStartClicked: () -> Unit,
    onPauseClicked: () -> Unit,
    onResumeClicked: () -> Unit,
    onStopClicked: () -> Unit,
    onDeleteRecordingClicked: () -> Unit = {},
    onAddRealtimeSegment: (String, SpeakerRole) -> Unit = { _, _ -> },
    onConsentRequested: () -> Unit,
    onViewSummaryClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    var showConsentModal by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var activeSpeaker by remember { mutableStateOf(SpeakerRole.PATIENT) }
    var manualInputText by remember { mutableStateOf("") }
    val speechRecognizer = rememberPlatformSpeechRecognizer()

    LaunchedEffect(recordingState, activeSpeaker) {
        if (recordingState == RecordingState.RECORDING) {
            speechRecognizer.startListening(
                onTextRecognized = { text ->
                    onAddRealtimeSegment(text, activeSpeaker)
                },
                onRmsChanged = { _ -> }
            )
        } else {
            speechRecognizer.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MelColors.AquaGreen.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎙️", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Consultation Scribe", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Real-Time Clinical Audio & Diarization", fontSize = 11.sp, color = MelColors.TextMuted)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MelColors.DeepBlue)
                    }
                },
                actions = {
                    if (recordingState != RecordingState.IDLE || transcriptSegments.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Text("🗑️", fontSize = 18.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MelColors.CardSurface)
            )
        },
        containerColor = MelColors.SurfaceBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Emergency Anchor & Status Pill
            Column(modifier = Modifier.fillMaxWidth()) {
                EmergencySafetyAnchor(emergencyNumber = "911", onEmergencyCallClicked = { /* Call 911 */ })
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (recordingState) {
                            RecordingState.RECORDING -> MelColors.Ivory
                            RecordingState.STOPPED -> MelColors.LavenderLight
                            else -> MelColors.SurfaceBackground
                        }
                    ),
                    border = BorderStroke(
                        1.dp,
                        when (recordingState) {
                            RecordingState.RECORDING -> MelColors.Apricot
                            RecordingState.STOPPED -> MelColors.Lavender
                            else -> MelColors.BorderSubtle
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when (recordingState) {
                                        RecordingState.RECORDING -> MelColors.AquaGreen
                                        RecordingState.PAUSED -> MelColors.Warning
                                        RecordingState.STOPPED -> MelColors.Success
                                        RecordingState.IDLE -> MelColors.TextMuted
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (recordingState) {
                                RecordingState.IDLE -> "Ready to capture medical consultation with consent."
                                RecordingState.RECORDING -> "Ambient capture active: listening to patient & provider..."
                                RecordingState.PAUSED -> "Capture paused. Tap Resume to continue."
                                RecordingState.STOPPED -> "Consultation captured and synced to secure Supabase."
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MelColors.TextPrimary
                        )
                    }
                }
            }

            // Central Area: Live Waveform Visualizer + Breathing Ambient Halo + Timer + Live Transcript
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Audio Status Bar: Timer, Halo, Waveform
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AmbientRecordingHalo(
                                isRecording = recordingState == RecordingState.RECORDING
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (recordingState == RecordingState.RECORDING) MelColors.AquaGreen else MelColors.SurfaceBackground
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (recordingState == RecordingState.RECORDING) "🎙️" else "⏸️",
                                        fontSize = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            val formattedTime = remember(elapsedSeconds) {
                                val minutes = elapsedSeconds / 60
                                val secs = elapsedSeconds % 60
                                "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
                            }

                            Column {
                                Text(
                                    text = formattedTime,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (recordingState == RecordingState.RECORDING) MelColors.DeepBlue else MelColors.TextMuted
                                )
                                Text(
                                    text = if (recordingState == RecordingState.RECORDING) "🔴 Live Audio Scribe" else "Recording Paused",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (recordingState == RecordingState.RECORDING) MelColors.CoralPink else MelColors.TextMuted
                                )
                            }
                        }

                        // Compact Audio Waveform Visualizer
                        Box(modifier = Modifier.width(120.dp).height(38.dp)) {
                            AudioWaveformVisualizer(
                                isRecording = recordingState == RecordingState.RECORDING,
                                amplitude = amplitude
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Diarized Live Transcript Section Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real-Time Spoken Dialogue (${transcriptSegments.size} turns)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.DeepBlue
                    )

                    if (isTranscribing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = MelColors.AquaGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Analyzing...", fontSize = 11.sp, color = MelColors.AquaGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Speaker Selection & Real-time Indicator
                if (recordingState == RecordingState.RECORDING) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = activeSpeaker == SpeakerRole.PATIENT,
                            onClick = { activeSpeaker = SpeakerRole.PATIENT },
                            label = { Text("👤 Patient Speaking", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MelColors.LightPinkLight,
                                selectedLabelColor = MelColors.CoralPink
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = activeSpeaker == SpeakerRole.PROVIDER,
                            onClick = { activeSpeaker = SpeakerRole.PROVIDER },
                            label = { Text("🩺 Doctor Speaking", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MelColors.Ivory,
                                selectedLabelColor = MelColors.DeepBlue
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Quick spoken / typed input bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { manualInputText = it },
                            placeholder = { Text("Speak or type spoken statement...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MelColors.AquaGreen,
                                unfocusedBorderColor = MelColors.BorderSubtle
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (manualInputText.isNotBlank()) {
                                    onAddRealtimeSegment(manualInputText.trim(), activeSpeaker)
                                    manualInputText = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen)
                        ) {
                            Text("+ Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (transcriptSegments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(MelColors.SurfaceBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, MelColors.BorderSubtle, RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎙️", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (recordingState == RecordingState.RECORDING) "Listening to Microphone..." else "Ready to Record Visit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MelColors.DeepBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (recordingState == RecordingState.RECORDING)
                                    "Speak near the phone. Words spoken by you or your doctor will transcribe live right here."
                                else
                                    "Tap 'Start Consultation Recording' when you are ready to begin.",
                                fontSize = 12.sp,
                                color = MelColors.TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(transcriptSegments) { segment ->
                            ClinicalTranscriptCard(segment)
                        }
                    }
                }
            }

            // Bottom Action Controls (WCAG 2.1 AA 48dp+ touch targets)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (recordingState) {
                    RecordingState.IDLE -> {
                        Button(
                            onClick = {
                                if (isConsentConfirmed) onStartClicked() else showConsentModal = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Start Consultation Recording", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    RecordingState.RECORDING -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = onPauseClicked,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Text("Pause", fontWeight = FontWeight.SemiBold)
                                }
                                Button(
                                    onClick = onStopClicked,
                                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.CoralPink),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Text("End & Transcribe", fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MelColors.CoralPink),
                                border = BorderStroke(1.dp, MelColors.CoralPink.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("🗑️ Discard & Delete Recording", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                    RecordingState.PAUSED -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = onResumeClicked,
                                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Text("Resume")
                                }
                                Button(
                                    onClick = onStopClicked,
                                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.CoralPink),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                ) {
                                    Text("Finish")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MelColors.CoralPink),
                                border = BorderStroke(1.dp, MelColors.CoralPink.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("🗑️ Discard & Delete Recording", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                    RecordingState.STOPPED -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = onViewSummaryClicked,
                                colors = ButtonDefaults.buttonColors(containerColor = MelColors.SkyBlue),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text("Generate Plain Medical Summary →", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MelColors.CoralPink),
                                border = BorderStroke(1.dp, MelColors.CoralPink.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("🗑️ Discard / Delete This Session", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConsentModal) {
        com.mel.medicalapp.presentation.components.ThreeTierConsentDialog(
            onDismissRequest = { showConsentModal = false },
            onConsentConfirmed = { _, _, _ ->
                showConsentModal = false
                onConsentRequested()
                onStartClicked()
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗑️ ", fontSize = 20.sp)
                    Text("Discard & Delete Recording?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to discard this recorded session? Any captured audio and live transcripts for this visit will be permanently removed.",
                    fontSize = 13.sp,
                    color = MelColors.TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteRecordingClicked()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.CoralPink),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Delete & Start Over", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = MelColors.TextMuted)
                }
            },
            containerColor = MelColors.CardSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ClinicalTranscriptCard(segment: TranscriptSegment) {
    val isPatient = segment.speaker == SpeakerRole.PATIENT
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPatient) MelColors.LightPinkLight else MelColors.Ivory
        ),
        border = BorderStroke(
            1.dp,
            if (isPatient) MelColors.LightPink.copy(alpha = 0.7f) else MelColors.Apricot.copy(alpha = 0.7f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isPatient) "👤 Patient Voice (You)" else "🩺 Healthcare Provider Note",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPatient) MelColors.CoralPink else MelColors.DeepBlue
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Conf: ${(segment.confidence * 100).toInt()}%",
                        fontSize = 10.sp,
                        color = MelColors.TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isPatient) "\"${segment.text}\"" else segment.text,
                fontSize = 13.sp,
                color = MelColors.TextPrimary,
                lineHeight = 19.sp,
                fontStyle = if (isPatient) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
            )
        }
    }
}
