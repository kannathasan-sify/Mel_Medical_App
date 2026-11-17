package com.mel.medicalapp.presentation.screens.questions

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mel.medicalapp.domain.model.FollowUpQuestion
import com.mel.medicalapp.domain.model.QuestionCategory
import com.mel.medicalapp.presentation.theme.MelColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowUpQuestionsScreen(
    questionsList: List<FollowUpQuestion>,
    onBackClicked: () -> Unit,
    onSaveForNextVisit: (List<FollowUpQuestion>) -> Unit
) {
    var questions by remember { mutableStateOf(questionsList) }
    var newQuestionInput by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var isAskDoctorMode by remember { mutableStateOf(false) } // Instruction 4: "Ask Doctor" High-Contrast Exam Room Mode

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MelColors.SkyBlue.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💬", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Follow-up Questions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Patient Empowerment Liaison", fontSize = 11.sp, color = MelColors.SkyBlue)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MelColors.DeepBlue)
                    }
                },
                actions = {
                    // "Ask Doctor Mode" High-Contrast Exam Room Toggle
                    Button(
                        onClick = { isAskDoctorMode = !isAskDoctorMode },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAskDoctorMode) MelColors.DeepBlue else MelColors.ApricotLight
                        ),
                        border = BorderStroke(1.dp, if (isAskDoctorMode) MelColors.DeepBlue else MelColors.Apricot),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(34.dp)
                    ) {
                        Text(
                            text = if (isAskDoctorMode) "Exit Doctor Mode" else "🩺 Ask Doctor Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAskDoctorMode) Color.White else MelColors.DeepBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MelColors.CardSurface)
            )
        },
        bottomBar = {
            if (!isAskDoctorMode) {
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
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Text("+ Custom Question", fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = { onSaveForNextVisit(questions) },
                            colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(52.dp)
                        ) {
                            Text("Save for Next Visit ✓", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = if (isAskDoctorMode) Color(0xFFF1F5F9) else MelColors.SurfaceBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isAskDoctorMode) {
                // High-Contrast Exam Room Banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.DeepBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🩺", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Exam Room Presentation Mode",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "High-contrast, large-text display designed to hand directly to your healthcare provider.",
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            } else {
                // Patient Empowerment Guidance Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                    border = BorderStroke(1.5.dp, MelColors.Apricot.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Prepared for Your Next Conversation",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MEL crafted these questions from your visit gaps. Check off answered questions, remove unneeded ones, or tap 'Ask Doctor Mode' when in the exam room.",
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Questions List (Standard or Large Exam-Room View)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (isAskDoctorMode) 14.dp else 10.dp)
            ) {
                items(questions) { question ->
                    if (isAskDoctorMode) {
                        ExamRoomQuestionCard(question)
                    } else {
                        QuestionItemCard(
                            question = question,
                            onToggleAnswered = {
                                questions = questions.map { q ->
                                    if (q.id == question.id) q.copy(isAnswered = !q.isAnswered) else q
                                }
                            },
                            onDelete = {
                                questions = questions.filterNot { it.id == question.id }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text("Add Custom Question", fontWeight = FontWeight.Bold, color = MelColors.DeepBlue)
            },
            text = {
                OutlinedTextField(
                    value = newQuestionInput,
                    onValueChange = { newQuestionInput = it },
                    label = { Text("What would you like to ask your provider?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newQuestionInput.isNotBlank()) {
                            val newQ = FollowUpQuestion(
                                id = "custom-${questions.size + 1}",
                                summaryId = "sum-current",
                                questionText = newQuestionInput.trim(),
                                category = QuestionCategory.GENERAL,
                                isPatientEdited = true
                            )
                            questions = questions + newQ
                            newQuestionInput = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MelColors.SkyBlue)
                ) {
                    Text("Add Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = MelColors.TextMuted)
                }
            }
        )
    }
}

/**
 * Standard View with Checkboxes and Categories
 */
@Composable
private fun QuestionItemCard(
    question: FollowUpQuestion,
    onToggleAnswered: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (question.isAnswered) MelColors.SurfaceBackground else MelColors.CardSurface
        ),
        border = BorderStroke(
            1.dp,
            if (question.isAnswered) MelColors.BorderSubtle else MelColors.SkyBlue.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = question.isAnswered,
                onCheckedChange = { onToggleAnswered() },
                colors = CheckboxDefaults.colors(checkedColor = MelColors.AquaGreen),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                val categoryColor = when (question.category) {
                    QuestionCategory.MEDICATION -> MelColors.CoralPink
                    QuestionCategory.CLARIFICATION -> MelColors.Lavender
                    QuestionCategory.NEXT_STEPS -> MelColors.SkyBlue
                    QuestionCategory.HISTORY -> MelColors.Apricot
                    QuestionCategory.GENERAL -> MelColors.AquaGreen
                }
                Box(
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = question.category.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = question.questionText,
                    fontSize = 13.sp,
                    fontWeight = if (question.isAnswered) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (question.isAnswered) MelColors.TextMuted else MelColors.TextPrimary,
                    lineHeight = 18.sp
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Text("✕", fontSize = 12.sp, color = MelColors.TextMuted)
            }
        }
    }
}

/**
 * Large-Type, High-Contrast Exam Room Presentation Card for Doctor
 */
@Composable
private fun ExamRoomQuestionCard(question: FollowUpQuestion) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(2.dp, MelColors.DeepBlue.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "[${question.category.name}]",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MelColors.SkyBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.questionText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MelColors.DeepBlue,
                lineHeight = 24.sp
            )
        }
    }
}
