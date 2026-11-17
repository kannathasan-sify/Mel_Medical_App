package com.mel.medicalapp.presentation.screens.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

enum class HomeBottomTab {
    APPOINTMENTS,
    CAPTURE_UPLOAD,
    SCRIBE,
    LEARN,
    RESOURCES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    patientName: String = "Patient",
    onAccountInfoClicked: () -> Unit,
    onStartScribeClicked: () -> Unit,
    onCaptureUploadClicked: () -> Unit,
    onAppointmentsClicked: () -> Unit,
    onLearnClicked: () -> Unit,
    onResourcesClicked: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(HomeBottomTab.SCRIBE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MelColors.ApricotLight, CircleShape)
                                .border(1.dp, MelColors.Apricot, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💪🩺", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ask MEL",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MelColors.DeepBlue
                            )
                            Text(
                                text = "Empowerment Liaison",
                                fontSize = 11.sp,
                                color = MelColors.AquaGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    // "Account Info" button from sketch top right
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MelColors.ApricotLight),
                        border = BorderStroke(1.dp, MelColors.Apricot),
                        modifier = Modifier
                            .clickable(onClick = onAccountInfoClicked)
                            .padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("☰ ", fontSize = 14.sp, color = MelColors.DeepBlue)
                            Text(
                                text = "Account Info",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MelColors.DeepBlue
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MelColors.CardSurface)
            )
        },
        bottomBar = {
            // High-fidelity bottom navigation tiles inspired directly by the sketch's 5 colored squares:
            // 1. Light Pink: Appointments + Reminders
            // 2. Coral Pink: Capture / Upload
            // 3. Aqua Green: Scribe (Record Visit / Take Notes)
            // 4. Sky Blue: Learn (Research, Review "What's that?")
            // 5. Lavender: Resources (Legal Info, Patient Rights)
            Surface(
                color = MelColors.CardSurface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SketchNavTile(
                        iconText = "📅",
                        label = "Appts",
                        tileColor = MelColors.LightPink,
                        isSelected = selectedTab == HomeBottomTab.APPOINTMENTS,
                        onClick = {
                            selectedTab = HomeBottomTab.APPOINTMENTS
                            onAppointmentsClicked()
                        }
                    )
                    SketchNavTile(
                        iconText = "📤",
                        label = "Upload",
                        tileColor = MelColors.CoralPink,
                        isSelected = selectedTab == HomeBottomTab.CAPTURE_UPLOAD,
                        onClick = {
                            selectedTab = HomeBottomTab.CAPTURE_UPLOAD
                            onCaptureUploadClicked()
                        }
                    )
                    SketchNavTile(
                        iconText = "🎙️",
                        label = "Scribe",
                        tileColor = MelColors.AquaGreen,
                        isSelected = selectedTab == HomeBottomTab.SCRIBE,
                        isCenterHero = true,
                        onClick = {
                            selectedTab = HomeBottomTab.SCRIBE
                            onStartScribeClicked()
                        }
                    )
                    SketchNavTile(
                        iconText = "🔍",
                        label = "Learn",
                        tileColor = MelColors.SkyBlue,
                        isSelected = selectedTab == HomeBottomTab.LEARN,
                        onClick = {
                            selectedTab = HomeBottomTab.LEARN
                            onLearnClicked()
                        }
                    )
                    SketchNavTile(
                        iconText = "⚖️",
                        label = "Rights",
                        tileColor = MelColors.Lavender,
                        isSelected = selectedTab == HomeBottomTab.RESOURCES,
                        onClick = {
                            selectedTab = HomeBottomTab.RESOURCES
                            onResourcesClicked()
                        }
                    )
                }
            }
        },
        containerColor = MelColors.SurfaceBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Welcome Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                border = BorderStroke(1.5.dp, MelColors.Apricot.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Hello, $patientName",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.DeepBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "MEL is ready to accompany your next appointment. Review notes, ask clarifying questions, and stay empowered.",
                        fontSize = 13.sp,
                        color = MelColors.TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            // Hero Action: Quick Start Consultation Scribe (Aqua Green from sketch)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.5.dp, MelColors.AquaGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onStartScribeClicked)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MelColors.AquaGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎙️", fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Record Medical Visit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Capture conversation with mutual consent & get instant plain notes.",
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary
                        )
                    }
                    Text("→", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MelColors.AquaGreen)
                }
            }

            // Feature Quick Tiles Row (Learn & Resources)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Learn / Research ("What's that?")
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.SkyBlue.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onLearnClicked)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🔍", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Learn & Review",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.SkyBlue
                        )
                        Text(
                            text = "Decode medical terms and test lab ranges in plain English.",
                            fontSize = 11.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Legal & Patient Rights (Lavender)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.Lavender.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onResourcesClicked)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("⚖️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Patient Rights",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.Lavender
                        )
                        Text(
                            text = "Your rights to records, second opinions, and respectful care.",
                            fontSize = 11.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SketchNavTile(
    iconText: String,
    label: String,
    tileColor: Color,
    isSelected: Boolean,
    isCenterHero: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (isCenterHero) 46.dp else 40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tileColor)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MelColors.DeepBlue else Color.DarkGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(iconText, fontSize = if (isCenterHero) 20.sp else 16.sp)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MelColors.DeepBlue else MelColors.TextSecondary
        )
    }
}

