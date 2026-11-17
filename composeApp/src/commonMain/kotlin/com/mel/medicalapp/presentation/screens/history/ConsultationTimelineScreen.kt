package com.mel.medicalapp.presentation.screens.history

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
import com.mel.medicalapp.domain.usecase.history.HistoricalVisitItem
import com.mel.medicalapp.presentation.theme.MelColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationTimelineScreen(
    visits: List<HistoricalVisitItem>,
    onVisitSelected: (String) -> Unit,
    onDeleteVisit: (String) -> Unit = {},
    onBackClicked: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Visits") }

    val filterOptions = listOf(
        "All Visits",
        "Urgent / ER",
        "Outpatient",
        "Cardiology"
    )

    val filteredVisits = visits.filter { visit ->
        val matchesCategory = when (selectedFilter) {
            "All Visits" -> true
            "Urgent / ER" -> visit.visitType.contains("Urgent", ignoreCase = true) || visit.visitType.contains("Emergency", ignoreCase = true)
            "Outpatient" -> visit.visitType.contains("Outpatient", ignoreCase = true)
            "Cardiology" -> visit.visitType.contains("Cardiology", ignoreCase = true) || visit.providerName.contains("Cardiolog", ignoreCase = true)
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
            visit.primaryFocus.contains(searchQuery, ignoreCase = true) ||
            visit.providerName.contains(searchQuery, ignoreCase = true) ||
            visit.visitType.contains(searchQuery, ignoreCase = true) ||
            visit.keyFindings.any { it.contains(searchQuery, ignoreCase = true) }

        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(MelColors.LightPink.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⏳", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Medical Visit History", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MelColors.DeepBlue)
                            Text("Chronological Health Journey & Summaries", fontSize = 11.sp, color = MelColors.TextMuted)
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
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by doctor, diagnosis, or symptom...") },
                placeholder = { Text("e.g. Pyelonephritis, Dr. Jenkins, Hernia...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MelColors.SkyBlue,
                    unfocusedBorderColor = MelColors.BorderSubtle
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
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

            // Header summary count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Documented Encounters (${filteredVisits.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MelColors.DeepBlue
                )
                Box(
                    modifier = Modifier
                        .background(MelColors.ApricotLight, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Auto-Synced",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MelColors.Apricot
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredVisits.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
                    border = BorderStroke(1.dp, MelColors.BorderSubtle),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching visits found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.DeepBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search filter or start a new consultation recording.",
                            fontSize = 12.sp,
                            color = MelColors.TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredVisits) { visit ->
                        TimelineVisitCard(
                            visit = visit,
                            onClick = { onVisitSelected(visit.id) },
                            onDelete = { onDeleteVisit(visit.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineVisitCard(
    visit: HistoricalVisitItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MelColors.CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MelColors.BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Date, Status Tag & Delete Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visit.dateDisplay,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MelColors.Apricot
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MelColors.AquaGreenLight)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = visit.visitType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MelColors.AquaGreenDark
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MelColors.CoralPink.copy(alpha = 0.12f))
                            .clickable { onDelete() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🗑️", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Primary Clinical Focus
            Text(
                text = visit.primaryFocus,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MelColors.DeepBlue
            )

            Text(
                text = "Provider: ${visit.providerName}",
                fontSize = 12.sp,
                color = MelColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Key Findings Highlights
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MelColors.SurfaceBackground, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Key Findings & Follow-ups:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MelColors.TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                visit.keyFindings.forEach { finding ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("• ", fontSize = 12.sp, color = MelColors.SkyBlue, fontWeight = FontWeight.Bold)
                        Text(
                            text = finding,
                            fontSize = 12.sp,
                            color = MelColors.TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Questions Count & View Summary CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💬 ", fontSize = 12.sp)
                    Text(
                        text = "${visit.pendingQuestionsCount} Follow-up Questions",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MelColors.SkyBlue
                    )
                }
                Text(
                    text = "View Plain Notes →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MelColors.DeepBlue
                )
            }
        }
    }
}
