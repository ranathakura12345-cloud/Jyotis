package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.model.*
import com.example.ui.components.NorthIndianKundaliCanvas
import com.example.ui.components.SouthIndianKundaliCanvas
import com.example.ui.theme.*

@Composable
fun KundaliScreen(
    activeProfile: UserProfileEntity?,
    getKundaliChart: (UserProfileEntity) -> KundaliChartData,
    onOpenProfileManager: () -> Unit
) {
    if (activeProfile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active profile. Tap above to create a Kundali.", color = StarlightMuted)
        }
        return
    }

    val chartData = remember(activeProfile) { getKundaliChart(activeProfile) }
    var chartStyle by remember { mutableStateOf(ChartStyle.NORTH_INDIAN) }
    var selectedHouseNum by remember { mutableStateOf(1) }

    val selectedHouse = chartData.houses.find { it.bhava.number == selectedHouseNum } ?: chartData.houses.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // 1. Profile Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = chartData.profileName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = VedicGoldLight
                    )
                    Text(
                        text = "${chartData.birthDate} at ${chartData.birthTime} • ${chartData.birthPlace}",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted
                    )
                }

                FilledTonalButton(
                    onClick = onOpenProfileManager,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CosmicSurfaceVariant,
                        contentColor = VedicGoldLight
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = "Switch", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Switch")
                }
            }
        }

        // 2. Vital Vedic Coordinates Strip (Lagna, Moon, Sun, Nakshatra)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lagna (Asc)", fontSize = 11.sp, color = StarlightMuted)
                        Text(chartData.lagnaRashi.sanskritName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                        Text(chartData.lagnaRashi.englishName, fontSize = 10.sp, color = StarlightMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Moon Sign", fontSize = 11.sp, color = StarlightMuted)
                        Text(chartData.chandraRashi.sanskritName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = VedicGoldLight)
                        Text(chartData.chandraRashi.englishName, fontSize = 10.sp, color = StarlightMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sun Sign", fontSize = 11.sp, color = StarlightMuted)
                        Text(chartData.suryaRashi.sanskritName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SaffronOrange)
                        Text(chartData.suryaRashi.englishName, fontSize = 10.sp, color = StarlightMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nakshatra", fontSize = 11.sp, color = StarlightMuted)
                        Text(chartData.nakshatra.sanskritName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CelestialCyan)
                        Text("Pada ${chartData.nakshatraPada}", fontSize = 10.sp, color = StarlightMuted)
                    }
                }
            }
        }

        // 3. Chart Style Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lagna Kundali Chart",
                    style = MaterialTheme.typography.titleMedium,
                    color = VedicGoldLight
                )

                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = chartStyle == ChartStyle.NORTH_INDIAN,
                        onClick = { chartStyle = ChartStyle.NORTH_INDIAN },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = VedicGoldContainer,
                            activeContentColor = VedicGoldLight
                        )
                    ) {
                        Text("North (Diamond)", fontSize = 11.sp)
                    }
                    SegmentedButton(
                        selected = chartStyle == ChartStyle.SOUTH_INDIAN,
                        onClick = { chartStyle = ChartStyle.SOUTH_INDIAN },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = VedicGoldContainer,
                            activeContentColor = VedicGoldLight
                        )
                    ) {
                        Text("South (Grid)", fontSize = 11.sp)
                    }
                }
            }
        }

        // 4. Interactive Kundali Canvas Chart
        item {
            if (chartStyle == ChartStyle.NORTH_INDIAN) {
                NorthIndianKundaliCanvas(
                    chartData = chartData,
                    selectedHouse = selectedHouseNum,
                    onHouseSelected = { selectedHouseNum = it },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                SouthIndianKundaliCanvas(
                    chartData = chartData,
                    onRashiSelected = { clickedRashi ->
                        val house = chartData.houses.find { it.rashi == clickedRashi }
                        if (house != null) selectedHouseNum = house.bhava.number
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 5. Selected House Inspector Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicCardElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "House ${selectedHouse.bhava.number}: ${selectedHouse.bhava.englishName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = VedicGoldLight
                            )
                            Text(
                                text = "${selectedHouse.bhava.sanskritName} (${selectedHouse.bhava.devanagari})",
                                style = MaterialTheme.typography.bodySmall,
                                color = SaffronOrange
                            )
                        }

                        Surface(
                            color = CosmicSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
                        ) {
                            Text(
                                text = "Rashi: ${selectedHouse.rashi.sanskritName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StarlightWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Significations: ${selectedHouse.bhava.significations}",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Karaka Planet: ",
                            style = MaterialTheme.typography.bodySmall,
                            color = StarlightMuted
                        )
                        Text(
                            text = "${selectedHouse.bhava.karakaPlanet.sanskritName} (${selectedHouse.bhava.karakaPlanet.devanagari})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = VedicGold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Occupying Grahas:",
                        style = MaterialTheme.typography.labelMedium,
                        color = StarlightWhite
                    )

                    if (selectedHouse.planets.isEmpty()) {
                        Text(
                            text = "No planets directly occupying this house. Governed by ${selectedHouse.rashi.lordSanskrit} (${selectedHouse.rashi.lordEnglish}).",
                            style = MaterialTheme.typography.bodySmall,
                            color = StarlightMuted,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            selectedHouse.planets.forEach { p ->
                                Surface(
                                    color = CosmicSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                        Text(
                                            text = "${p.planet.sanskritName} ${p.planet.symbol}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = VedicGoldLight
                                        )
                                        Text(
                                            text = p.dignity.label,
                                            fontSize = 10.sp,
                                            color = if (p.dignity == Dignity.EXALTED || p.dignity == Dignity.OWN) AuspiciousGreen else StarlightMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Navagraha Planetary Placements Table
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Navagraha Planetary Positions",
                        style = MaterialTheme.typography.titleMedium,
                        color = VedicGoldLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Table Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Planet", style = MaterialTheme.typography.labelSmall, color = StarlightMuted, modifier = Modifier.weight(1.2f))
                        Text("Rashi", style = MaterialTheme.typography.labelSmall, color = StarlightMuted, modifier = Modifier.weight(1.2f))
                        Text("House", style = MaterialTheme.typography.labelSmall, color = StarlightMuted, modifier = Modifier.weight(0.8f))
                        Text("Dignity", style = MaterialTheme.typography.labelSmall, color = StarlightMuted, modifier = Modifier.weight(1.4f))
                    }
                    HorizontalDivider(color = CosmicOutline)

                    chartData.planetPlacements.forEach { placement ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = placement.planet.symbol,
                                    fontSize = 14.sp,
                                    color = VedicGold,
                                    modifier = Modifier.width(18.dp)
                                )
                                Text(
                                    text = placement.planet.sanskritName.substringBefore('/'),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StarlightWhite
                                )
                            }
                            Text(
                                text = "${placement.rashi.sanskritName} ${placement.degreeInSign.toInt()}°",
                                style = MaterialTheme.typography.bodySmall,
                                color = StarlightWhite,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = "H${placement.houseNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = VedicGoldLight,
                                modifier = Modifier.weight(0.8f)
                            )
                            Text(
                                text = placement.dignity.label.substringBefore('(').trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = when (placement.dignity) {
                                    Dignity.EXALTED, Dignity.OWN -> AuspiciousGreen
                                    Dignity.DEBILITATED -> CriticalRed
                                    else -> StarlightMuted
                                },
                                modifier = Modifier.weight(1.4f)
                            )
                        }
                    }
                }
            }
        }

        // 7. Vimshottari Mahadasha Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vimshottari Dasha Period",
                            style = MaterialTheme.typography.titleMedium,
                            color = VedicGoldLight
                        )
                        Surface(
                            color = VedicGoldContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${chartData.currentDasha.startsYear} - ${chartData.currentDasha.endsYear}",
                                fontSize = 11.sp,
                                color = VedicGoldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Active: ${chartData.currentDasha.mahadasha.sanskritName} Mahadasha • ${chartData.currentDasha.antardasha.sanskritName} Antardasha",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = StarlightWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = chartData.currentDasha.effectSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 8. Manglik Dosha Analysis Card
        item {
            val manglik = chartData.manglikStatus
            val cardBg = if (!manglik.isManglik) CosmicSurfaceVariant else if (manglik.isCancelled) CosmicSurfaceVariant else Color(0xFF2B1414)
            val borderTint = if (!manglik.isManglik) AuspiciousGreen else if (manglik.isCancelled) WarningAmber else CriticalRed

            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderTint.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Manglik Dosha (Kuja Dosha)",
                            style = MaterialTheme.typography.titleMedium,
                            color = StarlightWhite
                        )
                        Surface(
                            color = borderTint.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (!manglik.isManglik) "Non-Manglik" else manglik.severity,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = borderTint,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = manglik.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightWhite,
                        lineHeight = 18.sp
                    )

                    if (manglik.isManglik && !manglik.isCancelled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prescribed Upaya: Recite Mangala Gayatri or Hanuman Chalisa every Tuesday.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SaffronOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
