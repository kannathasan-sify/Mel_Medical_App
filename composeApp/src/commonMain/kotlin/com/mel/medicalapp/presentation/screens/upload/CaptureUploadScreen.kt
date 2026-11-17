package com.mel.medicalapp.presentation.screens.upload

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.mel.medicalapp.platform.rememberPlatformMediaPicker
import com.mel.medicalapp.presentation.theme.MelColors

data class ClinicalDocumentSample(
    val title: String,
    val subtitle: String,
    val icon: String,
    val fileName: String,
    val extractedMedicalText: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureUploadScreen(
    onBackClicked: () -> Unit,
    onDocumentProcessed: (fileName: String, content: String) -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }
    var selectedDocName by remember { mutableStateOf<String?>(null) }
    var selectedDocContent by remember { mutableStateOf("") }

    val mediaPicker = rememberPlatformMediaPicker { fileName, content ->
        selectedDocName = fileName
        selectedDocContent = content
    }

    val sampleDocuments = remember {
        listOf(
            ClinicalDocumentSample(
                title = "Urinalysis & Lab Culture",
                subtitle = "Detects Pyelonephritis & Bacteria (PDF)",
                icon = "🧪",
                fileName = "Lab_Culture_Ascending_UTI.pdf",
                extractedMedicalText = "CLINICAL LAB REPORT:\n" +
                    "Specimen: Urine Culture & Sensitivity\n" +
                    "Findings: Nitrites POSITIVE, Leukocyte Esterase 3+ High.\n" +
                    "Colony Count: >100,000 CFU/mL Escherichia coli.\n" +
                    "Clinical Assessment: Acute Pyelonephritis secondary to untreated urinary tract infection with patient dysphagia to oral Nitrofurantoin.\n" +
                    "Recommendation: Immediate IV Ceftriaxone in Emergency Department."
            ),
            ClinicalDocumentSample(
                title = "Trauma & Radiology CT Scan",
                subtitle = "Reducible Umbilical Hernia finding",
                icon = "🩻",
                fileName = "Radiology_CT_Abdomen_Pelvis.pdf",
                extractedMedicalText = "RADIOLOGY REPORT:\n" +
                    "Procedure: CT Abdomen and Pelvis with IV Contrast\n" +
                    "Findings: 2.1cm anterior abdominal wall fascial defect 2cm inferior to umbilicus containing non-obstructed mesenteric fat. Reducible upon supine positioning.\n" +
                    "Impression: Reducible post-traumatic umbilical hernia. ENT referral recommended for concurrent right ear conductive fullness."
            ),
            ClinicalDocumentSample(
                title = "Emergency Discharge Instructions",
                subtitle = "Hospital after-care & medication orders",
                icon = "📋",
                fileName = "ER_Discharge_Summary_Orders.pdf",
                extractedMedicalText = "DISCHARGE SUMMARY:\n" +
                    "Admitting Diagnosis: Acute ascending flank pain and dehydration.\n" +
                    "Medications Prescribed: Intravenous hydration completed. Transitioned to liquid oral suspension due to documented pill dysphagia.\n" +
                    "Follow-up: Primary care follow-up required within 48-72 hours. Red-flag precautions given for high fever or worsening rigors."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MelColors.CoralPink.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📤", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Document Capture & Upload", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Lab Reports, Imaging & Discharge Papers", fontSize = 11.sp, color = MelColors.TextMuted)
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explanatory Banner
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MelColors.Ivory),
                border = BorderStroke(1.5.dp, MelColors.Apricot.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("💡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Translate Complex Medical Records",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Take a photo or choose a PDF of your lab results, after-visit discharge papers, or radiology scans. MEL translates medical jargon into plain English with follow-up questions.",
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Quick Actions: Camera Scan & File Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Action 1: Camera Scan
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            mediaPicker.launchCamera()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.5.dp, MelColors.CoralPink.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MelColors.CoralLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📸", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Take Photo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MelColors.DeepBlue)
                        Text("Scan Papers", fontSize = 11.sp, color = MelColors.TextMuted)
                    }
                }

                // Action 2: Choose File / PDF
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            mediaPicker.launchFilePicker()
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.5.dp, MelColors.SkyBlue.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MelColors.SkyBlueLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📁", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Choose File", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MelColors.DeepBlue)
                        Text("PDF, JPG, PNG", fontSize = 11.sp, color = MelColors.TextMuted)
                    }
                }
            }

            // Choose Document to Analyze
            Text(
                text = "Select Document to Translate & Analyze:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MelColors.DeepBlue
            )

            sampleDocuments.forEach { doc ->
                val isSelected = selectedDocName?.contains(doc.fileName) == true
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedDocName = doc.fileName
                            selectedDocContent = doc.extractedMedicalText
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MelColors.AquaGreenLight else MelColors.CardSurface
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MelColors.AquaGreen else MelColors.BorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(doc.icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MelColors.TextPrimary
                            )
                            Text(
                                text = doc.subtitle,
                                fontSize = 11.sp,
                                color = MelColors.TextMuted
                            )
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .background(MelColors.AquaGreen, CircleShape)
                                    .size(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Document Preview & OCR Text
            if (selectedDocName != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.5.dp, MelColors.AquaGreen)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "📄 ${selectedDocName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MelColors.DeepBlue
                                )
                            }
                            
                            // Delete / Remove Button
                            Box(
                                modifier = Modifier
                                    .background(MelColors.CoralPink.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedDocName = null
                                        selectedDocContent = ""
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🗑️", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MelColors.CoralPink)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = selectedDocContent,
                            fontSize = 12.sp,
                            color = MelColors.TextSecondary,
                            lineHeight = 17.sp,
                            modifier = Modifier
                                .background(MelColors.SurfaceBackground, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                // Dedicated Remove / Change button
                OutlinedButton(
                    onClick = {
                        selectedDocName = null
                        selectedDocContent = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MelColors.CoralPink),
                    border = BorderStroke(1.dp, MelColors.CoralPink.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("🗑️ Clear / Remove This Image/Document", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📄 No Document Selected", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MelColors.DeepBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a photo or choose a file above, or select a sample report below.",
                            fontSize = 11.sp,
                            color = MelColors.TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            Button(
                onClick = {
                    if (selectedDocName != null) {
                        isProcessing = true
                        onDocumentProcessed(selectedDocName!!, selectedDocContent)
                    }
                },
                enabled = selectedDocName != null && !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = MelColors.AquaGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Translating & Analyzing Document...", fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Translate & Analyze Findings 🚀", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
