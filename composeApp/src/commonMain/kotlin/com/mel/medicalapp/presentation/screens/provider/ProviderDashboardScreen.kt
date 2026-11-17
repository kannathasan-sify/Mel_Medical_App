package com.mel.medicalapp.presentation.screens.provider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

enum class ProviderTab {
    DASHBOARD,
    SHARED_VISITS,
    PATIENT_QUESTIONS,
    SETTINGS
}

data class AuthorizedPatientCard(
    val id: String,
    val name: String,
    val lastVisit: String,
    val scope: String,
    val pendingQuestionsCount: Int,
    val summaryReady: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    providerName: String = "Dr. Sarah Jenkins, MD",
    onSwitchToPatientMode: () -> Unit,
    onViewPatientSummary: (String) -> Unit
) {
    var activeTab by remember { mutableStateOf(ProviderTab.DASHBOARD) }

    val sharedPatients = remember {
        listOf(
            AuthorizedPatientCard(
                id = "pat-1",
                name = "Kannathasan Mariappan",
                lastVisit = "Nov 12, 2026",
                scope = "Full Summary + Questions",
                pendingQuestionsCount = 3,
                summaryReady = true
            ),
            AuthorizedPatientCard(
                id = "pat-2",
                name = "Eleanor Vance",
                lastVisit = "Nov 08, 2026",
                scope = "Medications Only",
                pendingQuestionsCount = 1,
                summaryReady = true
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MEL Provider Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.DeepBlue
                        )
                        Text(
                            text = providerName,
                            fontSize = 12.sp,
                            color = MelColors.TextMuted
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onSwitchToPatientMode,
                        colors = ButtonDefaults.buttonColors(containerColor = MelColors.ApricotLight),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Patient Mode 🔄",
                            color = MelColors.DeepBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MelColors.SurfaceBackground)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MelColors.CardSurface,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == ProviderTab.DASHBOARD,
                    onClick = { activeTab = ProviderTab.DASHBOARD },
                    icon = { Text("📋", fontSize = 18.sp) },
                    label = { Text("Dashboard", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == ProviderTab.SHARED_VISITS,
                    onClick = { activeTab = ProviderTab.SHARED_VISITS },
                    icon = { Text("🩺", fontSize = 18.sp) },
                    label = { Text("Shared Visits", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == ProviderTab.PATIENT_QUESTIONS,
                    onClick = { activeTab = ProviderTab.PATIENT_QUESTIONS },
                    icon = { Text("❓", fontSize = 18.sp) },
                    label = { Text("Questions", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == ProviderTab.SETTINGS,
                    onClick = { activeTab = ProviderTab.SETTINGS },
                    icon = { Text("⚙️", fontSize = 18.sp) },
                    label = { Text("Settings", fontSize = 11.sp) }
                )
            }
        },
        containerColor = MelColors.SurfaceBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // HIPAA Notice Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.SkyBlueLight),
                    border = BorderStroke(1.dp, MelColors.SkyBlue.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔒", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Authorized Patient Access Only",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MelColors.DeepBlue
                            )
                            Text(
                                text = "Records shown here have been explicitly authorized by patients under HIPAA/GDPR consent.",
                                fontSize = 11.sp,
                                color = MelColors.TextSecondary
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Shared Patient Consultations (${sharedPatients.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MelColors.DeepBlue
                )
            }

            items(sharedPatients) { patient ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onViewPatientSummary(patient.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = patient.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MelColors.TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .background(MelColors.AquaGreenLight, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Authorized",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MelColors.AquaGreenDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Last Visit: ${patient.lastVisit} · Scope: ${patient.scope}",
                            fontSize = 12.sp,
                            color = MelColors.TextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💬 ${patient.pendingQuestionsCount} Patient Questions",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MelColors.CoralPink
                            )

                            Button(
                                onClick = { onViewPatientSummary(patient.id) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MelColors.SkyBlue),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Review Summary", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

