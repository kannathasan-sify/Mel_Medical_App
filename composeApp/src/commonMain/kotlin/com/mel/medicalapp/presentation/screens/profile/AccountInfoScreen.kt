package com.mel.medicalapp.presentation.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

data class AccountProfileData(
    val email: String,
    val genderAtBirth: String = "Female",
    val ageGroup: String = "30-34",
    val summaryDetailLevel: String = "Moderate (Approx 8th grade)",
    val mfaMethod: String = "Phone Code",
    val incomingLanguage: String = "English",
    val outputLanguage: String = "English"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    userEmail: String,
    profileData: AccountProfileData = AccountProfileData(email = userEmail),
    onSaveAndClose: (AccountProfileData) -> Unit,
    onBack: () -> Unit,
    onSwitchToProviderPortal: () -> Unit = {}
) {
    var genderAtBirth by remember(profileData) { mutableStateOf(profileData.genderAtBirth) }
    var selectedAgeGroup by remember(profileData) { mutableStateOf(profileData.ageGroup) }
    var summaryDetailLevel by remember(profileData) { mutableStateOf(profileData.summaryDetailLevel) }
    var mfaMethod by remember(profileData) { mutableStateOf(profileData.mfaMethod) }
    var incomingLanguage by remember(profileData) { mutableStateOf(profileData.incomingLanguage) }
    var outputLanguage by remember(profileData) { mutableStateOf(profileData.outputLanguage) }

    val ageGroups = listOf(
        "18-24", "25-29", "30-34",
        "35-39", "40-44", "45-49",
        "50-54", "55-59", "60+"
    )

    val detailLevels = listOf(
        "Simple (Approx 5th grade level)",
        "Moderate (Approx 8th grade level)",
        "Complex (Approx 12th grade level)"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Info", fontWeight = FontWeight.Bold, color = MelColors.DeepBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val updated = AccountProfileData(
                                email = userEmail,
                                genderAtBirth = genderAtBirth,
                                ageGroup = selectedAgeGroup,
                                summaryDetailLevel = summaryDetailLevel,
                                mfaMethod = mfaMethod,
                                incomingLanguage = incomingLanguage,
                                outputLanguage = outputLanguage
                            )
                            onSaveAndClose(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MelColors.Apricot),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("Save + Close", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Credentials Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                border = BorderStroke(1.dp, MelColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("User Credentials", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MelColors.DeepBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Email: $userEmail", fontSize = 14.sp, color = MelColors.TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reset / Forgot Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MelColors.SkyBlue,
                        modifier = Modifier.clickable { /* trigger reset */ }
                    )
                }
            }

            // Gender & Age Group Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                border = BorderStroke(1.dp, MelColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Demographic Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MelColors.DeepBlue)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text("Gender at Birth:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("Female", "Male", "No response").forEach { option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = genderAtBirth == option,
                                    onClick = { genderAtBirth = option }
                                )
                                Text(option, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Age Group:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Grid of Age Groups
                    ageGroups.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row.forEach { age ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { selectedAgeGroup = age }
                                ) {
                                    RadioButton(
                                        selected = selectedAgeGroup == age,
                                        onClick = { selectedAgeGroup = age }
                                    )
                                    Text(age, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Medical Summary Detail Level (Direct from Sketch)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                border = BorderStroke(1.dp, MelColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Medical Summary Detail", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MelColors.DeepBlue)
                    Text("Adjust how MEL translates complex medical notes for you:", fontSize = 12.sp, color = MelColors.TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    detailLevels.forEach { level ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { summaryDetailLevel = level }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = summaryDetailLevel == level,
                                onClick = { summaryDetailLevel = level }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(level, fontSize = 13.sp, color = MelColors.TextPrimary)
                        }
                    }
                }
            }

            // Multi-Factor Authentication (MFA)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                border = BorderStroke(1.dp, MelColors.BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MFA (Multi-Factor Authentication)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MelColors.DeepBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = mfaMethod == "Phone Code",
                            onClick = { mfaMethod = "Phone Code" }
                        )
                        Text("Phone Code", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(20.dp))
                        RadioButton(
                            selected = mfaMethod == "Facial Scan",
                            onClick = { mfaMethod = "Facial Scan" }
                        )
                        Text("Facial Scan (Biometric)", fontSize = 13.sp)
                    }
                }
            }

            // Provider Portal Toggle
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.SkyBlueLight),
                border = BorderStroke(1.dp, MelColors.SkyBlue.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Healthcare Provider Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MelColors.DeepBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("If you are an authorized healthcare clinician, switch to review shared patient visits and questions.", fontSize = 12.sp, color = MelColors.TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onSwitchToProviderPortal,
                        colors = ButtonDefaults.buttonColors(containerColor = MelColors.SkyBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Switch to Provider View 🩺", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

