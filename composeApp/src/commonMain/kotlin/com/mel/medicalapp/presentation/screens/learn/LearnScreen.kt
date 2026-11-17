package com.mel.medicalapp.presentation.screens.learn

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.mel.medicalapp.presentation.theme.MelColors

data class MedicalTermExplanation(
    val id: String,
    val term: String,
    val pronunciation: String,
    val plainMeaning: String,
    val whyItMatters: String,
    val doctorQuestions: List<String>,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    onBackClicked: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedTermId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "All",
        "Kidney & Bladder",
        "Heart & Vessels",
        "Medications & Rx",
        "Imaging & Tests",
        "General Anatomy"
    )

    val allTerms = remember {
        mutableStateListOf(
            MedicalTermExplanation(
                id = "term-1",
                term = "Pyelonephritis",
                pronunciation = "pie-uh-low-nuh-FRY-tis",
                plainMeaning = "A severe bacterial kidney infection that typically begins in your bladder and ascends up into one or both kidneys.",
                whyItMatters = "Requires urgent antibiotic treatment (often IV) to prevent kidney scarring and blood infection (sepsis).",
                doctorQuestions = listOf(
                    "Are my kidneys working properly on my blood tests?",
                    "Do I need antibiotics through an IV or can I take pills?",
                    "When should my fever and flank pain start to improve?"
                ),
                category = "Kidney & Bladder"
            ),
            MedicalTermExplanation(
                id = "term-2",
                term = "Dysphagia",
                pronunciation = "dis-FAY-juh",
                plainMeaning = "Difficulty, pain, or hesitation when swallowing foods, liquids, or large medicine tablets.",
                whyItMatters = "Can cause patients to miss required pills (like antibiotics). Doctors can prescribe liquid suspensions or chewable alternatives.",
                doctorQuestions = listOf(
                    "Can you prescribe this medication as a liquid or patch?",
                    "Is it safe to crush these tablets with applesauce?",
                    "Should I see an ENT or speech therapist for a swallow evaluation?"
                ),
                category = "General Anatomy"
            ),
            MedicalTermExplanation(
                id = "term-3",
                term = "Reducible Hernia",
                pronunciation = "ree-DOO-suh-bul HER-nee-uh",
                plainMeaning = "An internal tissue or intestine bulge through a weak abdominal muscle spot that can be gently pushed back flat.",
                whyItMatters = "Safe to monitor, but if it becomes firm, discolored, or severely painful, it is an emergency.",
                doctorQuestions = listOf(
                    "What warning signs mean I should go straight to the ER?",
                    "Do I need an abdominal support belt or surgery?",
                    "What lifting weight limits should I follow?"
                ),
                category = "General Anatomy"
            ),
            MedicalTermExplanation(
                id = "term-4",
                term = "Cervical Strain (Whiplash)",
                pronunciation = "SUR-vih-kul STRAYN",
                plainMeaning = "Stretching or tearing of neck muscle fibers and tendons, common after sudden vehicle collision movement.",
                whyItMatters = "Can lead to tension headaches, muscle spasms, and can irritate nerves affecting balance or hearing sensations.",
                doctorQuestions = listOf(
                    "Would physical therapy or heat/ice therapy help?",
                    "Can this neck strain cause my ear fullness or dizziness?",
                    "What exercises can I do at home safely?"
                ),
                category = "General Anatomy"
            ),
            MedicalTermExplanation(
                id = "term-5",
                term = "Echocardiogram (Echo)",
                pronunciation = "ek-oh-kar-dee-oh-gram",
                plainMeaning = "A painless ultrasound that bounces sound waves off your heart to create moving pictures of your heart valves and pumping strength.",
                whyItMatters = "Checks how efficiently your heart pumps blood (ejection fraction) and whether any heart valves are leaking.",
                doctorQuestions = listOf(
                    "What was my heart's pumping percentage (ejection fraction)?",
                    "Did you see any stiff or leaking valves?",
                    "Do I need to repeat this test in 6 or 12 months?"
                ),
                category = "Heart & Vessels"
            ),
            MedicalTermExplanation(
                id = "term-6",
                term = "Hemoglobin A1c (HbA1c)",
                pronunciation = "hee-muh-GLOH-bin A-one-C",
                plainMeaning = "A simple blood test that reveals your average blood sugar level over the past 2 to 3 months.",
                whyItMatters = "Used to diagnose prediabetes and diabetes, and to verify if your current diet or medication is keeping blood sugar in a safe zone.",
                doctorQuestions = listOf(
                    "What is my current A1c number and what is my goal target?",
                    "Does my medication need adjustment based on this result?",
                    "When should we re-test my A1c?"
                ),
                category = "Imaging & Tests"
            ),
            MedicalTermExplanation(
                id = "term-7",
                term = "Nitrofurantoin (Macrobid)",
                pronunciation = "nye-troe-fyoor-AN-toyn",
                plainMeaning = "An antibiotic specifically designed to concentrate in urine to treat simple bacterial bladder infections.",
                whyItMatters = "Does not reach high enough levels in kidney tissue to cure kidney infections (pyelonephritis); take with food to prevent nausea.",
                doctorQuestions = listOf(
                    "Is this antibiotic strong enough if my infection has reached my kidneys?",
                    "What side effects should I watch out for?",
                    "Can I take this with meals?"
                ),
                category = "Medications & Rx"
            ),
            MedicalTermExplanation(
                id = "term-8",
                term = "Troponin Test",
                pronunciation = "troh-POH-nin",
                plainMeaning = "A blood protein that leaks into your bloodstream only when heart muscle cells have experienced stress or injury.",
                whyItMatters = "The gold standard ER lab test used to immediately identify or rule out a heart attack in patients with chest pain.",
                doctorQuestions = listOf(
                    "Were my troponin levels normal or elevated?",
                    "Will we run a second troponin test in 2-3 hours to be sure?",
                    "What else could have caused my symptoms?"
                ),
                category = "Heart & Vessels"
            ),
            MedicalTermExplanation(
                id = "term-9",
                term = "Creatinine & eGFR",
                pronunciation = "kree-AT-ih-nin / G-F-R",
                plainMeaning = "Blood measurements showing how effectively your kidneys are filtering waste products from your blood.",
                whyItMatters = "Normal kidney filtration (eGFR above 60) is necessary before receiving certain IV dyes, medications, or CT contrast.",
                doctorQuestions = listOf(
                    "Are my kidneys filtering at a healthy rate?",
                    "Do any of my medications put stress on my kidneys?",
                    "Should I drink more water before my next lab draw?"
                ),
                category = "Kidney & Bladder"
            )
        )
    }

    val filteredTerms = allTerms.filter { item ->
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        val matchesQuery = searchQuery.isBlank() ||
            item.term.contains(searchQuery, ignoreCase = true) ||
            item.plainMeaning.contains(searchQuery, ignoreCase = true) ||
            item.category.contains(searchQuery, ignoreCase = true) ||
            item.whyItMatters.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

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
                            Text("🔍", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Learn: \"What's That?\"", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Dynamic Medical Terms Decoded in Plain English", fontSize = 11.sp, color = MelColors.TextMuted)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search any medical word, test, lab, or medicine...") },
                placeholder = { Text("e.g. Pyelonephritis, A1c, Echo, Hernia...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MelColors.SkyBlue,
                    unfocusedBorderColor = MelColors.BorderSubtle
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MelColors.SkyBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Terms Count or Empty Helper
            if (filteredTerms.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                    border = BorderStroke(1.dp, MelColors.Apricot.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🤖", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ask MEL AI to Decode \"$searchQuery\"",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "MEL's clinical intelligence engine can explain any medical term, lab panel, or prescription in plain 5th-to-8th grade language.",
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 17.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                allTerms.add(
                                    MedicalTermExplanation(
                                        id = "custom-${allTerms.size + 1}",
                                        term = searchQuery.capitalizeWords(),
                                        pronunciation = "Medical term",
                                        plainMeaning = "A clinical finding or medical concept relating to patient care and symptoms.",
                                        whyItMatters = "Understanding this helps you participate actively in treatment planning and ask targeted questions.",
                                        doctorQuestions = listOf(
                                            "What does $searchQuery mean for my daily health?",
                                            "Do I need any follow-up tests or medication changes?"
                                        ),
                                        category = "General Health"
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Add & Explain with MEL AI ✨", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTerms) { termItem ->
                        val isExpanded = expandedTermId == termItem.id
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                            border = BorderStroke(
                                width = if (isExpanded) 1.5.dp else 1.dp,
                                color = if (isExpanded) MelColors.SkyBlue else MelColors.BorderSubtle
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedTermId = if (isExpanded) null else termItem.id
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = termItem.term,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MelColors.DeepBlue
                                        )
                                        Text(
                                            text = "🗣️ ${termItem.pronunciation}",
                                            fontSize = 12.sp,
                                            color = MelColors.SkyBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MelColors.SkyBlueLight, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = termItem.category,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MelColors.SkyBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = termItem.plainMeaning,
                                    fontSize = 13.sp,
                                    color = MelColors.TextPrimary,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Why it matters section
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MelColors.SurfaceBackground, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Why this matters to you:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MelColors.CoralPink
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = termItem.whyItMatters,
                                            fontSize = 12.sp,
                                            color = MelColors.TextSecondary,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                // Expandable Doctor Questions
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "💬 Questions to ask your doctor about this:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MelColors.DeepBlue
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    termItem.doctorQuestions.forEach { question ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("• ", fontWeight = FontWeight.Bold, color = MelColors.AquaGreen)
                                            Text(
                                                text = question,
                                                fontSize = 12.sp,
                                                color = MelColors.TextPrimary,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Text(
                                        text = if (isExpanded) "Show Less ▲" else "Questions to Ask Doctor ▼",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MelColors.SkyBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }
