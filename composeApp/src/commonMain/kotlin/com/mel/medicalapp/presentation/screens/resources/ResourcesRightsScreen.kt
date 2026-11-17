package com.mel.medicalapp.presentation.screens.resources

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

data class PatientRightTopic(
    val title: String,
    val summary: String,
    val actionableTips: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesRightsScreen(
    onBackClicked: () -> Unit
) {
    val rights = listOf(
        PatientRightTopic(
            title = "Right to Informed Consent & Full Explanation",
            summary = "You have the legal right to know why a treatment is recommended, the common risks, and what non-surgical or alternative medications are available.",
            actionableTips = listOf(
                "You can ask: 'What are the alternatives to this treatment?'",
                "You have the right to refuse a treatment or test without being denied care.",
                "Doctors must explain in language you understand before proceeding."
            )
        ),
        PatientRightTopic(
            title = "Right to Complete Copies of Your Medical Records",
            summary = "Under HIPAA (US) and GDPR (EU), you have an unconditional legal right to inspect and obtain copies of your clinical notes, lab results, and imaging reports.",
            actionableTips = listOf(
                "Providers cannot refuse records because you owe an outstanding medical bill.",
                "You can request records in electronic format (PDF or portal export).",
                "You have the right to request corrections if a diagnosis is inaccurate."
            )
        ),
        PatientRightTopic(
            title = "Right to a Second Medical Opinion",
            summary = "Seeking a second opinion is standard medical practice and a fundamental patient right, especially for persistent or unresolving symptoms.",
            actionableTips = listOf(
                "Good providers encourage and respect second opinions.",
                "You can say: 'I’d like to have another specialist review these test results to ensure we have explored all options.'"
            )
        ),
        PatientRightTopic(
            title = "Right to Respectful, Non-Discriminatory Healthcare",
            summary = "You are entitled to considerate and respectful care free from discrimination based on race, gender identity, sexual orientation, disability, income, or language.",
            actionableTips = listOf(
                "You have the right to a certified medical interpreter at no personal cost.",
                "You have the right to designate an advocate or family member to accompany you."
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(MelColors.Lavender.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚖️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Patient Rights & Resources", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Legal Protections & Patient Advocacy", fontSize = 11.sp, color = MelColors.Lavender)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MelColors.DeepBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MelColors.CardSurface)
            )
        },
        containerColor = MelColors.SurfaceBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.LavenderLight),
                    border = BorderStroke(1.dp, MelColors.Lavender),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Empowering Your Healthcare Journey",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Healthcare can feel intimidating or rushed. Understanding your legal rights ensures you remain an active, confident partner in your own healthcare decisions.",
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            items(rights) { right ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = right.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = right.summary,
                            fontSize = 13.sp,
                            color = MelColors.TextPrimary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MelColors.SurfaceBackground, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Actionable Steps You Can Take:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MelColors.Lavender
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            right.actionableTips.forEach { tip ->
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("✓ ", fontSize = 12.sp, color = MelColors.AquaGreen, fontWeight = FontWeight.Bold)
                                    Text(tip, fontSize = 12.sp, color = MelColors.TextSecondary, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

