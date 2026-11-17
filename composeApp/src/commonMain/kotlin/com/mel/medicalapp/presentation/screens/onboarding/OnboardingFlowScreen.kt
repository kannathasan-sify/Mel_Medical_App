package com.mel.medicalapp.presentation.screens.onboarding

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.presentation.theme.MelColors

enum class OnboardingStep(val stepNumber: Int, val totalSteps: Int = 6) {
    WELCOME(0),
    PRIMARY_CHALLENGE(1),
    GENDER_IDENTITY(2),
    RANK_VALUES(3),
    IDENTITY_GROUPS(4),
    PRIVACY_LEGAL(5)
}

@Composable
fun OnboardingFlowScreen(
    onOnboardingCompleted: (OnboardingData) -> Unit
) {
    var currentStep by remember { mutableStateOf(OnboardingStep.PRIMARY_CHALLENGE) }
    val onboardingData = remember { mutableStateOf(OnboardingData()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MelColors.SurfaceBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Progress Tracker (from sketch: "PROGRESS TRACKER")
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step ${currentStep.stepNumber} of ${currentStep.totalSteps - 1}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.DeepBlue
                    )
                    Text(
                        text = "Onboarding",
                        fontSize = 12.sp,
                        color = MelColors.TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { currentStep.stepNumber.toFloat() / (currentStep.totalSteps - 1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MelColors.Apricot,
                    trackColor = MelColors.BorderSubtle
                )
            }

            // Step Content inside Framed Card (Apricot border from sketches)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                border = BorderStroke(2.dp, MelColors.Apricot),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    when (currentStep) {
                        OnboardingStep.WELCOME -> {}
                        OnboardingStep.PRIMARY_CHALLENGE -> {
                            StepPrimaryChallenge(
                                selected = onboardingData.value.primaryChallenge,
                                onSelect = { onboardingData.value = onboardingData.value.copy(primaryChallenge = it) }
                            )
                        }
                        OnboardingStep.GENDER_IDENTITY -> {
                            StepGenderIdentity(
                                selected = onboardingData.value.genderAtBirth,
                                onSelect = { onboardingData.value = onboardingData.value.copy(genderAtBirth = it) }
                            )
                        }
                        OnboardingStep.RANK_VALUES -> {
                            StepRankValues(
                                rankedMap = onboardingData.value.rankedValues,
                                onRankChanged = { valName, rank ->
                                    val updated = onboardingData.value.rankedValues.toMutableMap()
                                    updated[valName] = rank
                                    onboardingData.value = onboardingData.value.copy(rankedValues = updated)
                                }
                            )
                        }
                        OnboardingStep.IDENTITY_GROUPS -> {
                            StepIdentityGroups(
                                selectedGroups = onboardingData.value.identityGroups,
                                onToggle = { group ->
                                    val current = onboardingData.value.identityGroups.toMutableSet()
                                    if (current.contains(group)) current.remove(group) else current.add(group)
                                    onboardingData.value = onboardingData.value.copy(identityGroups = current)
                                }
                            )
                        }
                        OnboardingStep.PRIVACY_LEGAL -> {
                            StepPrivacyLegal()
                        }
                    }
                }
            }

            // Bottom Navigation (Next / Save)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep.stepNumber > 1) {
                    TextButton(
                        onClick = {
                            val prevIndex = currentStep.ordinal - 1
                            if (prevIndex >= 1) currentStep = OnboardingStep.values()[prevIndex]
                        }
                    ) {
                        Text("← Back", color = MelColors.TextSecondary)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (currentStep == OnboardingStep.PRIVACY_LEGAL) {
                            onOnboardingCompleted(onboardingData.value)
                        } else {
                            val nextIndex = currentStep.ordinal + 1
                            if (nextIndex < OnboardingStep.values().size) {
                                currentStep = OnboardingStep.values()[nextIndex]
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.SkyBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = if (currentStep == OnboardingStep.PRIVACY_LEGAL) "Finish & Enter App" else "Next →",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: Q1 What is the biggest challenge? [Select one]
// -------------------------------------------------------------
@Composable
private fun StepPrimaryChallenge(
    selected: String?,
    onSelect: (String) -> Unit
) {
    val challenges = listOf(
        "Being heard / understood",
        "Managing next steps in process",
        "Understanding what the person is saying – too many terms that are confusing",
        "Being rushed"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Q1. Onboarding",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.Apricot
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "What is the biggest challenge you face during health related appointments?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.DeepBlue
        )
        Text(
            text = "[Select one]",
            fontSize = 13.sp,
            color = MelColors.TextMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        challenges.forEach { challenge ->
            val isChecked = selected == challenge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(challenge) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MelColors.LightPinkLight else MelColors.SurfaceBackground
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isChecked) MelColors.LightPink else MelColors.BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isChecked,
                        onClick = { onSelect(challenge) },
                        colors = RadioButtonDefaults.colors(selectedColor = MelColors.LightPink)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = challenge,
                        fontSize = 14.sp,
                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                        color = MelColors.TextPrimary
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: Q2 Gender assigned at birth
// -------------------------------------------------------------
@Composable
private fun StepGenderIdentity(
    selected: String?,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "Female" to "female",
        "Male" to "male",
        "Prefer not to share *" to "prefer_not_to_share"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Q2. Onboarding",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.Apricot
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "What was your gender assigned at birth?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.DeepBlue
        )

        Spacer(modifier = Modifier.height(20.dp))

        options.forEach { (label, value) ->
            val isChecked = selected == value
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelect(value) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MelColors.ApricotLight else MelColors.SurfaceBackground
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isChecked) MelColors.Apricot else MelColors.BorderSubtle
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isChecked,
                        onClick = { onSelect(value) },
                        colors = RadioButtonDefaults.colors(selectedColor = MelColors.Apricot)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                        color = MelColors.TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Note from sketch
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
            border = BorderStroke(1.dp, MelColors.BorderSubtle)
        ) {
            Text(
                text = "* Note: Clinical guidance and tailored lab reference ranges may be more challenging to customize if you choose not to share.",
                fontSize = 12.sp,
                color = MelColors.TextSecondary,
                lineHeight = 17.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 3: Q3 Rank values from 1 (Most important) to 5 (Least)
// -------------------------------------------------------------
@Composable
private fun StepRankValues(
    rankedMap: Map<String, Int>,
    onRankChanged: (String, Int) -> Unit
) {
    val values = listOf("Autonomy", "Honesty", "Collaboration", "Knowledge", "Trust")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Q3. Onboarding",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.Apricot
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Rank each of these values below from 1 – 5",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.DeepBlue
        )
        Text(
            text = "1 = Most important to you  •  5 = Least important to you",
            fontSize = 12.sp,
            color = MelColors.SkyBlue,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        values.forEach { valName ->
            val currentRank = rankedMap[valName] ?: 3
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
                border = BorderStroke(1.dp, MelColors.BorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = valName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.DeepBlue
                    )

                    // 1 to 5 Pill Ranker
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        (1..5).forEach { rank ->
                            val isSelected = currentRank == rank
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MelColors.SkyBlue else Color.White)
                                    .border(1.dp, if (isSelected) MelColors.SkyBlue else MelColors.BorderSubtle, CircleShape)
                                    .clickable { onRankChanged(valName, rank) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$rank",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MelColors.TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: Q5 Identity Groups (Select as many as apply)
// -------------------------------------------------------------
@Composable
private fun StepIdentityGroups(
    selectedGroups: Set<String>,
    onToggle: (String) -> Unit
) {
    val groups = listOf(
        "LGBTQIA+", "Immigrant", "Disabled", "Woman / Female",
        "Limited Income", "Mental Illness", "Cognitive Impairment",
        "Addiction / Substance Use", "Ethnicity", "Unemployed",
        "Race", "Unhoused", "Color", "Other"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Q5. Onboarding",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.Apricot
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Do you identify as part of any of these groups?",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.DeepBlue
        )
        Text(
            text = "(Select as many as apply. Helps MEL tailor advocacy language.)",
            fontSize = 12.sp,
            color = MelColors.TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(groups.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { group ->
                        val isChecked = selectedGroups.contains(group)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onToggle(group) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked) MelColors.ApricotLight else MelColors.SurfaceBackground
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isChecked) MelColors.Apricot else MelColors.BorderSubtle
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onToggle(group) },
                                    colors = CheckboxDefaults.colors(checkedColor = MelColors.Apricot)
                                )
                                Text(
                                    text = group,
                                    fontSize = 12.sp,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                    color = MelColors.TextPrimary
                                )
                            }
                        }
                    }
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: Privacy Statements and Other Legal Statements
// -------------------------------------------------------------
@Composable
private fun StepPrivacyLegal() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Privacy & Legal Notice",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MelColors.DeepBlue
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MelColors.SurfaceBackground),
            border = BorderStroke(1.dp, MelColors.BorderSubtle)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Your Health Data Belongs to You",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MelColors.AquaGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "MEL is committed to the highest standards of confidentiality under HIPAA (US) and GDPR (EU). Consultation audio and notes are strictly protected via Supabase Row Level Security. We never share or sell your medical encounters to third-party advertisers.\n\n* Privacy statements are available online anytime for review, export, or print in your Account settings.",
                    fontSize = 13.sp,
                    color = MelColors.TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

data class OnboardingData(
    val primaryChallenge: String? = null,
    val genderAtBirth: String? = null,
    val rankedValues: Map<String, Int> = emptyMap(),
    val identityGroups: Set<String> = emptySet()
)

