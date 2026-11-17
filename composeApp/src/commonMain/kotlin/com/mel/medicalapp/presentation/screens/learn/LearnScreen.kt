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
import com.mel.medicalapp.core.result.NetworkResult
import com.mel.medicalapp.data.remote.MelBackendApiClient
import com.mel.medicalapp.presentation.theme.MelColors
import kotlinx.coroutines.launch

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
    var isSearchingAi by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val backendApiClient = remember { MelBackendApiClient() }

    val categories = listOf(
        "All",
        "Heart & Vessels",
        "Diabetes & Metabolism",
        "Kidney & Bladder",
        "Medications & Rx",
        "Imaging & Tests",
        "General Health"
    )

    val allTerms = remember {
        mutableStateListOf(
            MedicalTermExplanation(
                id = "term-htn",
                term = "Hypertension (High Blood Pressure)",
                pronunciation = "hy-per-TEN-shun",
                plainMeaning = "When the force of blood pushing against the walls of your blood vessels is consistently too high. Often called a 'silent' condition because it rarely causes symptoms on its own.",
                whyItMatters = "Uncontrolled high blood pressure strains your heart, damages kidney filters, and significantly increases the risk of heart attacks and strokes.",
                doctorQuestions = listOf(
                    "What is my target blood pressure number for my age and health?",
                    "What time of day is best to take my blood pressure reading at home?",
                    "Are there dietary changes (like sodium reduction) that can lower my numbers?"
                ),
                category = "Heart & Vessels"
            ),
            MedicalTermExplanation(
                id = "term-t2d",
                term = "Type 2 Diabetes & Insulin Resistance",
                pronunciation = "DYE-uh-bee-teez",
                plainMeaning = "A chronic metabolic condition where your body's cells resist insulin or your pancreas doesn't make enough insulin, causing sugar (glucose) to build up in your bloodstream.",
                whyItMatters = "Keeping blood sugar in a safe range prevents nerve numbness (neuropathy), vision damage (retinopathy), and kidney disease.",
                doctorQuestions = listOf(
                    "What should my target blood sugar range be before and after meals?",
                    "How often should we check my Hemoglobin A1c level?",
                    "What symptoms of low blood sugar (hypoglycemia) should I watch out for?"
                ),
                category = "Diabetes & Metabolism"
            ),
            MedicalTermExplanation(
                id = "term-a1c",
                term = "Hemoglobin A1c (HbA1c)",
                pronunciation = "HEE-muh-gloh-bin A-one-C",
                plainMeaning = "A simple routine blood test that shows your average blood sugar level over the past 2 to 3 months, reflecting how much glucose is attached to red blood cells.",
                whyItMatters = "Normal is below 5.7%. Pre-diabetes is 5.7% to 6.4%, and 6.5% or higher on two separate tests indicates diabetes. Used to guide medication adjustments.",
                doctorQuestions = listOf(
                    "What was my most recent A1c percentage?",
                    "Is my current treatment plan keeping my A1c within our target goal?",
                    "When is my next A1c check scheduled?"
                ),
                category = "Imaging & Tests"
            ),
            MedicalTermExplanation(
                id = "term-lipid",
                term = "Lipid Panel & Cholesterol (LDL vs HDL)",
                pronunciation = "LIP-id PAN-ul",
                plainMeaning = "A blood test measuring fats in your blood: LDL ('bad' cholesterol that forms plaque), HDL ('good' cholesterol that clears arteries), and triglycerides.",
                whyItMatters = "High LDL cholesterol causes plaque buildup (atherosclerosis) inside your arteries, restricting healthy blood flow to your heart and brain.",
                doctorQuestions = listOf(
                    "What are my current LDL, HDL, and triglyceride levels?",
                    "Do my numbers warrant starting or adjusting a statin medication?",
                    "How much can exercise and heart-healthy fats improve my profile?"
                ),
                category = "Heart & Vessels"
            ),
            MedicalTermExplanation(
                id = "term-metformin",
                term = "Metformin",
                pronunciation = "met-FOR-min",
                plainMeaning = "A widely prescribed oral medication that lowers blood sugar by reducing the amount of glucose your liver produces and helping your body absorb insulin better.",
                whyItMatters = "First-line therapy for type 2 diabetes. Often causes mild stomach upset if not taken with food, and requires routine monitoring of kidney function.",
                doctorQuestions = listOf(
                    "Should I take this with my largest meal to avoid an upset stomach?",
                    "Is an extended-release (ER) version available if I experience nausea?",
                    "Are my kidney lab tests (eGFR) healthy enough for my current dose?"
                ),
                category = "Medications & Rx"
            ),
            MedicalTermExplanation(
                id = "term-lisinopril",
                term = "Lisinopril (ACE Inhibitor)",
                pronunciation = "lye-SIN-oh-pril",
                plainMeaning = "A medication that relaxes blood vessels so blood flows more easily, lowering blood pressure and protecting kidney function in diabetic patients.",
                whyItMatters = "Very effective for heart and kidney protection. A dry, persistent cough is a known side effect that can easily be switched to an ARB alternative.",
                doctorQuestions = listOf(
                    "What should I do if I develop a dry, nagging cough?",
                    "Do we need to check my potassium and creatinine levels after starting this?",
                    "Can this medication cause lightheadedness when standing up quickly?"
                ),
                category = "Medications & Rx"
            ),
            MedicalTermExplanation(
                id = "term-pyelo",
                term = "Pyelonephritis (Kidney Infection)",
                pronunciation = "pie-uh-low-nuh-FRY-tis",
                plainMeaning = "A severe bacterial infection in one or both kidneys that typically begins as a bladder infection (UTI) and travels upward.",
                whyItMatters = "Requires prompt antibiotic therapy (often IV in urgent care or the emergency department) to prevent permanent kidney scarring or blood infection (sepsis).",
                doctorQuestions = listOf(
                    "Are my kidneys filtering waste properly on my blood and urine tests?",
                    "Do I need IV antibiotics or are oral medications sufficient?",
                    "How soon should my flank pain and fever begin to resolve?"
                ),
                category = "Kidney & Bladder"
            ),
            MedicalTermExplanation(
                id = "term-dysphagia",
                term = "Dysphagia (Swallowing Difficulty)",
                pronunciation = "dis-FAY-juh",
                plainMeaning = "Difficulty, discomfort, or hesitation when swallowing foods, liquids, or large solid medication tablets.",
                whyItMatters = "Frequently causes patients to skip important pills (like antibiotics). Doctors and pharmacists can readily provide liquid suspensions, patches, or crushable alternatives.",
                doctorQuestions = listOf(
                    "Can you prescribe this medication as an oral liquid, dissolvable tablet, or patch?",
                    "Is it safe to crush these pills and mix them with applesauce?",
                    "Should I have a formal swallow evaluation with a speech therapist?"
                ),
                category = "General Health"
            ),
            MedicalTermExplanation(
                id = "term-egfr",
                term = "eGFR & Creatinine (Kidney Function)",
                pronunciation = "kree-AT-ih-nin / e-G-F-R",
                plainMeaning = "Creatinine is a waste product from muscle breakdown, and eGFR calculates how many milliliters of blood your kidneys clean every minute.",
                whyItMatters = "An eGFR above 60 indicates healthy kidney filtration. Knowing your score is essential before taking certain NSAIDs, diabetes pills, or receiving CT contrast dyes.",
                doctorQuestions = listOf(
                    "What is my current eGFR score, and has it been stable over time?",
                    "Do any of my regular prescriptions place strain on my kidneys?",
                    "How much water should I drink daily to support healthy filtration?"
                ),
                category = "Kidney & Bladder"
            ),
            MedicalTermExplanation(
                id = "term-ecg",
                term = "Electrocardiogram (ECG / EKG)",
                pronunciation = "ee-lec-tro-CAR-dee-oh-gram",
                plainMeaning = "A quick, painless test where sticky sensor patches placed on your chest record the electrical signals that make your heart beat.",
                whyItMatters = "Detects irregular heart rhythms (arrhythmias like Atrial Fibrillation), signs of prior or ongoing heart attacks, and heart muscle enlargement.",
                doctorQuestions = listOf(
                    "Did my EKG show normal rhythm and heart electrical conduction?",
                    "Do my heart palpitations warrant a 24-hour Holter monitor?",
                    "How does this EKG compare to my baseline from previous visits?"
                ),
                category = "Imaging & Tests"
            ),
            MedicalTermExplanation(
                id = "term-cbc",
                term = "Complete Blood Count (CBC)",
                pronunciation = "com-PLEET blood count",
                plainMeaning = "A foundational blood test that counts your red blood cells (carry oxygen), white blood cells (fight infection), and platelets (clot blood).",
                whyItMatters = "Identifies anemia (low red blood cells causing fatigue), active infections (high white blood cell count), and bleeding risks.",
                doctorQuestions = listOf(
                    "Are any of my red or white blood cell counts outside the normal reference range?",
                    "Does my hemoglobin level indicate iron deficiency or anemia?",
                    "Are my platelet levels adequate for routine procedures?"
                ),
                category = "Imaging & Tests"
            ),
            MedicalTermExplanation(
                id = "term-ct",
                term = "Computed Tomography (CT Scan)",
                pronunciation = "com-PYOO-ted toh-MOG-ruh-fee",
                plainMeaning = "A specialized medical scan that combines multiple X-ray images taken from different angles to create cross-sectional 3D slices of bones, organs, and blood vessels.",
                whyItMatters = "Provides much higher detail than standard X-rays to detect internal bleeding, organ inflammation, kidney stones, and tumors.",
                doctorQuestions = listOf(
                    "Will my CT scan require oral or IV contrast dye?",
                    "Were my kidney blood tests checked before receiving contrast?",
                    "What did the radiologist note regarding my abdominal or chest organs?"
                ),
                category = "Imaging & Tests"
            )
        )
    }

    fun generateClientFallbackExplanation(term: String): MedicalTermExplanation {
        val cleanTerm = term.trim().capitalizeWords()
        val lower = cleanTerm.lowercase()
        val cat = when {
            lower.contains("cardio") || lower.contains("heart") || lower.contains("arter") || lower.contains("blood pressure") || lower.contains("pulse") -> "Heart & Vessels"
            lower.contains("sugar") || lower.contains("diabet") || lower.contains("insulin") || lower.contains("metabol") -> "Diabetes & Metabolism"
            lower.contains("kidney") || lower.contains("nephr") || lower.contains("urine") || lower.contains("bladder") -> "Kidney & Bladder"
            lower.contains("pill") || lower.contains("tab") || lower.contains("mg") || lower.contains("statin") || lower.contains("cillin") || lower.contains("antibiotic") -> "Medications & Rx"
            lower.contains("scan") || lower.contains("ray") || lower.contains("mri") || lower.contains("ct") || lower.contains("echo") || lower.contains("panel") || lower.contains("lab") || lower.contains("count") -> "Imaging & Tests"
            else -> "General Health"
        }

        return MedicalTermExplanation(
            id = "ai-term-${cleanTerm.hashCode()}",
            term = cleanTerm,
            pronunciation = cleanTerm,
            plainMeaning = "A clinical finding, diagnostic test, or medication referenced during medical care regarding $cleanTerm. Understanding this empowers you to collaborate more closely with your physician and avoid misunderstandings.",
            whyItMatters = "Knowledge of $cleanTerm helps ensure your symptoms are accurately discussed, medical tests are clear, and treatment plans are followed safely.",
            doctorQuestions = listOf(
                "How does $cleanTerm specifically relate to my health symptoms or lab results?",
                "Are there specific target numbers or warning signs regarding $cleanTerm I should track at home?",
                "What are the recommended medical next steps or medication adjustments?",
                "When should we re-evaluate this in our next follow-up appointment?"
            ),
            category = cat
        )
    }

    fun searchAndExplainWithAi(query: String) {
        if (query.isBlank()) return
        val termToLookup = query.trim()
        coroutineScope.launch {
            isSearchingAi = true
            try {
                val res = backendApiClient.explainMedicalTerm(termToLookup)
                val explanation = when (res) {
                    is NetworkResult.Success -> {
                        val data = res.data
                        MedicalTermExplanation(
                            id = "ai-term-${data.term.hashCode()}",
                            term = data.term.capitalizeWords(),
                            pronunciation = data.pronunciation.ifBlank { data.term },
                            plainMeaning = data.plainMeaning,
                            whyItMatters = data.whyItMatters,
                            doctorQuestions = data.doctorQuestions.ifEmpty {
                                listOf(
                                    "How does ${data.term} affect my diagnosis or treatment?",
                                    "What warning signs should I watch out for at home?",
                                    "Do we need any follow-up tests or medication adjustments?"
                                )
                            },
                            category = data.category
                        )
                    }
                    else -> generateClientFallbackExplanation(termToLookup)
                }
                allTerms.removeAll { it.term.equals(explanation.term, ignoreCase = true) }
                allTerms.add(0, explanation)
                expandedTermId = explanation.id
            } catch (_: Exception) {
                val fallback = generateClientFallbackExplanation(termToLookup)
                allTerms.removeAll { it.term.equals(fallback.term, ignoreCase = true) }
                allTerms.add(0, fallback)
                expandedTermId = fallback.id
            } finally {
                isSearchingAi = false
            }
        }
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
            // Search Input with Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search any medical word, test, lab, or medicine...") },
                    placeholder = { Text("e.g. Metformin, A1c, CT Scan, Sepsis, Echo...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MelColors.SkyBlue,
                        unfocusedBorderColor = MelColors.BorderSubtle
                    ),
                    singleLine = true
                )
                if (searchQuery.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { searchAndExplainWithAi(searchQuery) },
                        colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        Text("Explain ✨", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic AI Decode Banner if searching
            if (searchQuery.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { searchAndExplainWithAi(searchQuery) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                    border = BorderStroke(1.dp, MelColors.AquaGreen)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text("🤖", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ask MEL AI: \"$searchQuery\"",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MelColors.DeepBlue
                                )
                                Text(
                                    text = "Tap for full phonetic pronunciation, plain meaning & doctor questions",
                                    fontSize = 10.sp,
                                    color = MelColors.TextMuted
                                )
                            }
                        }
                        if (isSearchingAi) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MelColors.AquaGreen)
                        } else {
                            Text("Generate →", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MelColors.AquaGreen)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

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

            Spacer(modifier = Modifier.height(10.dp))

            // Terms Count Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Medical Dictionary (${filteredTerms.size} terms)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MelColors.DeepBlue
                )
                Text(
                    text = "Tap to view doctor questions",
                    fontSize = 11.sp,
                    color = MelColors.TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredTerms.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                    border = BorderStroke(1.dp, MelColors.Apricot.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🤖", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Decode \"$searchQuery\" with MEL AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "MEL's clinical AI can decode any medical diagnosis, procedure, lab value, or drug into clear plain language with customized questions for your physician.",
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 17.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { searchAndExplainWithAi(searchQuery) },
                            enabled = !isSearchingAi,
                            colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            if (isSearchingAi) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Decoding Term with Clinical AI...", fontSize = 13.sp)
                            } else {
                                Text("Generate Full Clinical Explanation ✨", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
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
                                            fontSize = 15.sp,
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
