package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.model.CompatibilityReport
import com.example.model.KootaScore
import com.example.model.MatchingGrade
import com.example.ui.components.ScoreGauge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompatibilityScreen(
    profiles: List<UserProfileEntity>,
    onCalculateCompatibility: (UserProfileEntity, UserProfileEntity) -> CompatibilityReport,
    onAddNewProfileClick: () -> Unit
) {
    if (profiles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No profiles available. Please add at least 2 profiles to analyze compatibility.", color = StarlightMuted)
        }
        return
    }

    var person1 by remember(profiles) { mutableStateOf(profiles.first()) }
    var person2 by remember(profiles) {
        mutableStateOf(if (profiles.size > 1) profiles[1] else profiles[0])
    }

    val report = remember(person1, person2) {
        onCalculateCompatibility(person1, person2)
    }

    var showPerson1Dropdown by remember { mutableStateOf(false) }
    var showPerson2Dropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = "Kundali Milan",
                    style = MaterialTheme.typography.headlineMedium,
                    color = VedicGoldLight
                )
                Text(
                    text = "Ashtakoota 36-Gun Milan & Manglik Compatibility Analysis",
                    style = MaterialTheme.typography.bodySmall,
                    color = StarlightMuted
                )
            }
        }

        // 2. Profile Selectors Row
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Partners for Match",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGoldLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Person 1 Selector
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = showPerson1Dropdown,
                                onExpandedChange = { showPerson1Dropdown = it }
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    color = CosmicCardElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Partner 1", fontSize = 10.sp, color = StarlightMuted)
                                        Text(
                                            text = person1.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StarlightWhite,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${person1.getMoonRashi().sanskritName} • ${person1.getNakshatra().sanskritName}",
                                            fontSize = 10.sp,
                                            color = VedicGoldLight
                                        )
                                    }
                                }
                                ExposedDropdownMenu(
                                    expanded = showPerson1Dropdown,
                                    onDismissRequest = { showPerson1Dropdown = false },
                                    modifier = Modifier.background(CosmicSurfaceVariant)
                                ) {
                                    profiles.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p.name, color = StarlightWhite) },
                                            onClick = {
                                                person1 = p
                                                showPerson1Dropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Match",
                            tint = MysticRuby,
                            modifier = Modifier.size(24.dp)
                        )

                        // Person 2 Selector
                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = showPerson2Dropdown,
                                onExpandedChange = { showPerson2Dropdown = it }
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    color = CosmicCardElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Partner 2", fontSize = 10.sp, color = StarlightMuted)
                                        Text(
                                            text = person2.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StarlightWhite,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${person2.getMoonRashi().sanskritName} • ${person2.getNakshatra().sanskritName}",
                                            fontSize = 10.sp,
                                            color = VedicGoldLight
                                        )
                                    }
                                }
                                ExposedDropdownMenu(
                                    expanded = showPerson2Dropdown,
                                    onDismissRequest = { showPerson2Dropdown = false },
                                    modifier = Modifier.background(CosmicSurfaceVariant)
                                ) {
                                    profiles.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p.name, color = StarlightWhite) },
                                            onClick = {
                                                person2 = p
                                                showPerson2Dropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onAddNewProfileClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VedicGoldLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add New Kundali to Match")
                    }
                }
            }
        }

        // 3. Score Gauge & Verdict Banner
        item {
            val gradeColor = Color(report.grade.ratingColorHex)

            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicCardElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, gradeColor.copy(alpha = 0.7f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreGauge(
                        score = report.totalScore,
                        maxScore = report.maxScore,
                        label = "Gunas",
                        subLabel = "Milan Score",
                        sizeDp = 130.dp,
                        strokeWidth = 11.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = gradeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = report.grade.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = gradeColor,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = report.grade.verdict,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightWhite,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CosmicOutline)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Manglik Compatibility Notice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (report.person1IsManglik == report.person2IsManglik) AuspiciousGreen else WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = report.manglikCompatibility,
                            style = MaterialTheme.typography.bodySmall,
                            color = StarlightWhite
                        )
                    }
                }
            }
        }

        // 4. Detailed 8 Ashtakootas Breakdown Table
        item {
            Text(
                text = "Ashtakoota 8-Pillars Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = VedicGoldLight
            )
        }

        items(report.kootas) { koota ->
            val isFullScore = koota.pointsObtained == koota.maxPoints
            val isZero = koota.pointsObtained == 0.0

            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isZero) CriticalRed.copy(alpha = 0.5f) else CosmicOutline
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = koota.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = StarlightWhite
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${koota.devanagari})",
                                    fontSize = 11.sp,
                                    color = VedicGold
                                )
                            }
                            Text(
                                text = koota.attributeCompared,
                                style = MaterialTheme.typography.bodySmall,
                                color = StarlightMuted
                            )
                        }

                        // Score Pill
                        val scoreBg = if (isFullScore) AuspiciousGreen else if (isZero) CriticalRed else WarningAmber
                        Surface(
                            color = scoreBg.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${if (koota.pointsObtained % 1.0 == 0.0) koota.pointsObtained.toInt() else koota.pointsObtained} / ${koota.maxPoints.toInt()} pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = scoreBg,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = koota.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted,
                        lineHeight = 16.sp
                    )

                    if (koota.remedyIfLow != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = CosmicCardElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Remedy: ${koota.remedyIfLow}",
                                fontSize = 11.sp,
                                color = SaffronOrange,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Relationship Strengths & Remedies
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Relationship Pillars & Strengths",
                        style = MaterialTheme.typography.titleSmall,
                        color = AuspiciousGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    report.relationshipStrengths.forEach { str ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("✓ ", color = AuspiciousGreen, fontSize = 13.sp)
                            Text(text = str, style = MaterialTheme.typography.bodySmall, color = StarlightWhite)
                        }
                    }

                    if (report.potentialFrictionAreas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Areas for Mindful Communication",
                            style = MaterialTheme.typography.titleSmall,
                            color = WarningAmber
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        report.potentialFrictionAreas.forEach { fr ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                Text("! ", color = WarningAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = fr, style = MaterialTheme.typography.bodySmall, color = StarlightWhite)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vedic Harmony Remedies (Upayas)",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGoldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    report.suggestedRemedies.forEach { rem ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("✦ ", color = VedicGold, fontSize = 13.sp)
                            Text(text = rem, style = MaterialTheme.typography.bodySmall, color = StarlightWhite)
                        }
                    }
                }
            }
        }
    }
}
