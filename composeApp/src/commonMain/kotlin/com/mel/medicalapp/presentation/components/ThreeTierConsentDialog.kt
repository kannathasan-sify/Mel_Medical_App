package com.mel.medicalapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

@Composable
fun ThreeTierConsentDialog(
    onDismissRequest: () -> Unit,
    onConsentConfirmed: (audioRecording: Boolean, aiProcessing: Boolean, providerSharing: Boolean) -> Unit
) {
    var audioRecordingChecked by remember { mutableStateOf(true) }
    var aiProcessingChecked by remember { mutableStateOf(true) }
    var providerSharingChecked by remember { mutableStateOf(false) }

    val canProceed = audioRecordingChecked && aiProcessingChecked

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🛡️ ", fontSize = 20.sp)
                Column {
                    Text(
                        text = "Clinical Consent Authorization",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.DeepBlue
                    )
                    Text(
                        text = "HIPAA & GDPR Three-Tier Consent",
                        fontSize = 11.sp,
                        color = MelColors.TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "MEL protects your medical privacy. Please review each distinct authorization level below before recording starts:",
                    fontSize = 12.sp,
                    color = MelColors.TextSecondary
                )

                // 1. Audio Recording Consent
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = audioRecordingChecked,
                            onCheckedChange = { audioRecordingChecked = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "1. Audio Recording Consent (Required)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MelColors.TextPrimary
                            )
                            Text(
                                text = "I and my healthcare provider mutually agree to audio recording of this consultation for medical documentation purposes.",
                                fontSize = 11.sp,
                                color = MelColors.TextMuted
                            )
                        }
                    }
                }

                // 2. AI Processing Authorization
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = aiProcessingChecked,
                            onCheckedChange = { aiProcessingChecked = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "2. AI Processing Authorization (Required)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MelColors.TextPrimary
                            )
                            Text(
                                text = "I authorize MEL's clinical AI to convert speech to text, extract medical entities, and format plain-language summaries.",
                                fontSize = 11.sp,
                                color = MelColors.TextMuted
                            )
                        }
                    }
                }

                // 3. Provider Sharing Authorization
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = providerSharingChecked,
                            onCheckedChange = { providerSharingChecked = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "3. Provider Sharing Authorization (Optional)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MelColors.TextPrimary
                            )
                            Text(
                                text = "Allow my verified physician to access this visit summary and answer my generated follow-up questions in the portal.",
                                fontSize = 11.sp,
                                color = MelColors.TextMuted
                            )
                        }
                    }
                }

                Text(
                    text = "You may revoke consent at any time in Profile & Privacy settings.",
                    fontSize = 11.sp,
                    color = MelColors.TextMuted
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConsentConfirmed(audioRecordingChecked, aiProcessingChecked, providerSharingChecked)
                },
                enabled = canProceed,
                colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Authorize & Start Visit", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = MelColors.TextMuted)
            }
        },
        containerColor = MelColors.CardSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

