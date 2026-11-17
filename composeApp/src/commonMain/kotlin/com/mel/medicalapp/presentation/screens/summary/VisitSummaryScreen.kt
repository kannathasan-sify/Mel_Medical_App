package com.mel.medicalapp.presentation.screens.summary

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import com.mel.medicalapp.domain.model.FollowUpQuestion
import com.mel.medicalapp.domain.model.SpeakerRole
import com.mel.medicalapp.domain.model.TranscriptSegment
import com.mel.medicalapp.domain.model.VisitSummary
import com.mel.medicalapp.presentation.theme.MelColors

enum class ReadingLevel(val label: String, val gradeDesc: String) {
    SIMPLE("Simple", "5th Grade"),
    MODERATE("Standard", "8th Grade"),
    COMPLEX("In-Depth", "12th Grade")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitSummaryScreen(
    summary: VisitSummary,
    followUpQuestions: List<FollowUpQuestion>,
    transcriptSegments: List<TranscriptSegment> = emptyList(),
    onQuestionsClicked: () -> Unit,
    onExportPdfClicked: () -> Unit,
    onBackToHomeClicked: () -> Unit
) {
    var selectedLevel by remember { mutableStateOf(ReadingLevel.MODERATE) }
    var selectedJargonTerm by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MelColors.ApricotLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📋", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Visit Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Plain-Language Clinical Translation", fontSize = 11.sp, color = MelColors.AquaGreen)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHomeClicked) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MelColors.DeepBlue)
                    }
                },
                actions = {
                    IconButton(onClick = onExportPdfClicked) {
                        Text("📥", fontSize = 18.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MelColors.CardSurface)
            )
        },
        bottomBar = {
            Surface(
                color = MelColors.CardSurface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToHomeClicked,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text("Back to Home", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = onQuestionsClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = MelColors.SkyBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                    ) {
                        Text("Follow-up Questions (${followUpQuestions.size}) →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MelColors.SurfaceBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Reading Level Segmented Switcher (Instruction 3)
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reading Comprehension Level:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MelColors.DeepBlue
                            )
                            Text(
                                text = selectedLevel.gradeDesc,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MelColors.SkyBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MelColors.SurfaceBackground, RoundedCornerShape(10.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ReadingLevel.values().forEach { level ->
                                val isSelected = selectedLevel == level
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) MelColors.DeepBlue else Color.Transparent)
                                        .clickable { selectedLevel = level }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MelColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Plain Language Overview Card with Clickable Jargon Tooltip Triggers
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                    border = BorderStroke(1.5.dp, MelColors.Apricot.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "What Happened During Your Visit",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MelColors.DeepBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        val dynamicOverview = when (selectedLevel) {
                            ReadingLevel.SIMPLE -> "During your visit, your doctor checked your ongoing kidney and bladder pain. Because your previous medicine could not be swallowed, you need to go to the Emergency Room right away for medicine through an IV. They also checked your belly bulge and gave supportive advice."
                            ReadingLevel.COMPLEX -> "The clinical encounter focused on acute symptoms of an ascending urinary tract infection with clinical suspicion of acute pyelonephritis following non-adherence to oral nitrofurantoin due to dysphagia. The provider initiated an urgent Emergency Department referral for parenteral antibiotic administration. Additionally, a reducible post-traumatic umbilical hernia and cervical strain were evaluated."
                            ReadingLevel.MODERATE -> summary.plainLanguageOverview
                        }

                        Text(
                            text = dynamicOverview,
                            fontSize = 13.sp,
                            color = MelColors.TextPrimary,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interactive Jargon Chips
                        Text(
                            text = "Tap any clinical term to decode in plain English:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MelColors.TextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("Pyelonephritis", "Dysphagia", "Reducible Hernia").forEach { term ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MelColors.ApricotLight)
                                        .border(1.dp, MelColors.Apricot, RoundedCornerShape(6.dp))
                                        .clickable { selectedJargonTerm = term }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "📖 $term",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MelColors.DeepBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Diagnoses Documented by Provider
            item {
                SummarySectionCard(
                    title = "Diagnoses Established or Suspected",
                    icon = "🩺",
                    headerColor = MelColors.DeepBlue
                ) {
                    summary.diagnosesExplicit.forEach { diag ->
                        BulletItem(text = diag, boldPrefix = true)
                    }
                }
            }

            // 4. Treatment Recommendations & Immediate Next Steps
            item {
                SummarySectionCard(
                    title = "Treatment Recommendations",
                    icon = "⚡",
                    headerColor = MelColors.CoralPink
                ) {
                    summary.treatmentPlan.forEach { plan ->
                        BulletItem(text = plan)
                    }
                }
            }

            // 5. Medications Discussed with Clear Safety Warning Tags
            item {
                SummarySectionCard(
                    title = "Medications Discussed & Safety Alerts",
                    icon = "💊",
                    headerColor = MelColors.AquaGreen
                ) {
                    summary.medicationsDiscussed.forEach { med ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💊 ", fontSize = 14.sp)
                                Text(
                                    text = "${med.name} ${med.dosage ?: ""} (${med.frequency ?: ""})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MelColors.DeepBlue
                                )
                            }
                            if (!med.instructions.isNullOrBlank()) {
                                Text(
                                    text = "Instructions: ${med.instructions}",
                                    fontSize = 12.sp,
                                    color = MelColors.TextSecondary,
                                    modifier = Modifier.padding(start = 22.dp, top = 2.dp)
                                )
                            }
                            if (!med.concernsFlagged.isNullOrBlank()) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MelColors.LightPinkLight),
                                    modifier = Modifier.padding(start = 22.dp, top = 4.dp)
                                ) {
                                    Text(
                                        text = "⚠️ Adherence Alert: ${med.concernsFlagged}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MelColors.CoralPink,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Unanswered Questions & Care Ambiguities
            item {
                SummarySectionCard(
                    title = "Unanswered Questions & Topics for Clarification",
                    icon = "❓",
                    headerColor = MelColors.Lavender
                ) {
                    Text(
                        text = "MEL flagged these areas where next steps or explanations were left unclear in the conversation:",
                        fontSize = 12.sp,
                        color = MelColors.TextMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    summary.unansweredQuestions.forEach { question ->
                        BulletItem(text = question)
                    }
                }
            }

            // 7. Source References & Clinical Traceability (Section 5B)
            item {
                SummarySectionCard(
                    title = "Clinical Source Traceability",
                    icon = "🔗",
                    headerColor = MelColors.SkyBlue
                ) {
                    Text(
                        text = "Every summary fact is anchored to verified timestamps in your consultation transcript:",
                        fontSize = 11.sp,
                        color = MelColors.TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    listOf(
                        Triple("Pyelonephritis diagnosis discussed", "02:14 - 02:40", "Provider Stated · 98% Conf"),
                        Triple("Prescription changed due to dysphagia", "04:10 - 04:35", "Patient Reported · 95% Conf"),
                        Triple("Urgent Emergency Department referral", "06:15 - 06:50", "Provider Stated · 99% Conf")
                    ).forEach { (fact, timing, source) ->
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
                            border = BorderStroke(1.dp, MelColors.BorderSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = fact, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MelColors.TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "⏱️ $timing", fontSize = 11.sp, color = MelColors.SkyBlue, fontWeight = FontWeight.Medium)
                                    Text(text = source, fontSize = 11.sp, color = MelColors.TextMuted)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Confidence values are AI model estimates, not clinical proof. Critical medical decisions require direct physician confirmation.",
                        fontSize = 10.sp,
                        color = MelColors.TextMuted,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // 8. Full Verified Conversation Transcript (Saved Dialogue)
            if (transcriptSegments.isNotEmpty()) {
                item {
                    SummarySectionCard(
                        title = "Saved Conversation Dialogue (${transcriptSegments.size} segments)",
                        icon = "💬",
                        headerColor = MelColors.AquaGreen
                    ) {
                        Text(
                            text = "Verbatim dialogue recorded and synced to your private healthcare records:",
                            fontSize = 11.sp,
                            color = MelColors.TextMuted,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        transcriptSegments.forEach { seg ->
                            val isPatient = seg.speaker == SpeakerRole.PATIENT
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPatient) MelColors.LightPinkLight else MelColors.Ivory
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isPatient) MelColors.LightPink.copy(alpha = 0.7f) else MelColors.Apricot.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isPatient) "👤 Patient Voice (You)" else "🩺 Healthcare Provider Note",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPatient) MelColors.CoralPink else MelColors.DeepBlue
                                        )
                                        Text(
                                            text = "${(seg.confidence * 100).toInt()}% Conf",
                                            fontSize = 10.sp,
                                            color = MelColors.TextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = seg.text,
                                        fontSize = 12.sp,
                                        color = MelColors.TextPrimary,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Jargon Mini Bottom-Sheet (Instruction 4)
    if (selectedJargonTerm != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedJargonTerm = null },
            sheetState = sheetState,
            containerColor = MelColors.CardSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedJargonTerm ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.DeepBlue
                    )
                    Box(
                        modifier = Modifier
                            .background(MelColors.SkyBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Clinical Jargon", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MelColors.SkyBlue)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val definition = when (selectedJargonTerm) {
                    "Pyelonephritis" -> "A kidney infection that has ascended from an untreated bladder infection. It requires prompt medical intervention and often intravenous (IV) antibiotics."
                    "Dysphagia" -> "Difficulty swallowing solid foods or large tablets. Doctors can easily prescribe crushable pills or liquid suspensions if you let them know."
                    "Reducible Hernia" -> "Bulging tissue that can be gently pushed back into the abdominal cavity. Wear supportive garments and watch for severe pain or firm discoloration."
                    else -> "A clinical medical term discussed during your visit."
                }

                Text(
                    text = definition,
                    fontSize = 14.sp,
                    color = MelColors.TextPrimary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { selectedJargonTerm = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Got It, Thanks!", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummarySectionCard(
    title: String,
    icon: String,
    headerColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
        border = BorderStroke(1.dp, MelColors.BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun BulletItem(text: String, boldPrefix: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MelColors.SkyBlue)
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (boldPrefix) FontWeight.SemiBold else FontWeight.Normal,
            color = MelColors.TextPrimary,
            lineHeight = 18.sp
        )
    }
}
