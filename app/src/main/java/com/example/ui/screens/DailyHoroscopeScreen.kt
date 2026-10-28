package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.model.DailyHoroscope
import com.example.model.PanchangData
import com.example.model.Rashi
import com.example.ui.components.ScoreGauge
import com.example.ui.theme.*
import java.time.LocalDate

@Composable
fun DailyHoroscopeScreen(
    activeProfile: UserProfileEntity?,
    getHoroscopeForRashi: (Rashi) -> DailyHoroscope,
    getPanchang: () -> PanchangData,
    onOpenProfileManager: () -> Unit
) {
    val userRashi = activeProfile?.getMoonRashi() ?: Rashi.MESHA
    var selectedRashi by remember(activeProfile) { mutableStateOf(userRashi) }
    val horoscope = remember(selectedRashi) { getHoroscopeForRashi(selectedRashi) }
    val panchang = remember { getPanchang() }

    var japaCounter by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // 1. Header with Active Profile and Today's Date
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vedic Rashifal",
                        style = MaterialTheme.typography.headlineMedium,
                        color = VedicGoldLight
                    )
                    Text(
                        text = panchang.dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted
                    )
                }

                // Profile Chip
                Surface(
                    onClick = onOpenProfileManager,
                    color = CosmicSurfaceVariant,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = VedicGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeProfile?.name ?: "Select Chart",
                            style = MaterialTheme.typography.labelMedium,
                            color = StarlightWhite
                        )
                    }
                }
            }
        }

        // 2. Zodiac Sign Selector Carousel
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Rashi (Zodiac)",
                        style = MaterialTheme.typography.titleSmall,
                        color = StarlightMuted
                    )
                    if (selectedRashi != userRashi) {
                        Text(
                            text = "My Sign: ${userRashi.sanskritName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = VedicGold,
                            modifier = Modifier.clickable { selectedRashi = userRashi }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(Rashi.entries) { rashi ->
                        val isSelected = rashi == selectedRashi
                        val isUserSign = rashi == userRashi

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedRashi = rashi },
                            color = if (isSelected) VedicGoldContainer else CosmicSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) VedicGold else if (isUserSign) CelestialCyan.copy(alpha = 0.5f) else CosmicOutline
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = rashi.glyph,
                                    fontSize = 18.sp,
                                    color = if (isSelected) VedicGoldLight else StarlightWhite
                                )
                                Text(
                                    text = rashi.sanskritName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) VedicGoldLight else StarlightWhite
                                )
                                Text(
                                    text = rashi.englishName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StarlightMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Cosmic Energy Overview Card with ScoreGauge
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScoreGauge(
                        score = horoscope.cosmicEnergyScore.toDouble(),
                        maxScore = 100.0,
                        label = "Prana",
                        subLabel = "Energy",
                        sizeDp = 110.dp,
                        strokeWidth = 10.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${selectedRashi.sanskritName} (${selectedRashi.englishName})",
                                style = MaterialTheme.typography.titleMedium,
                                color = VedicGoldLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedRashi.element.sanskrit,
                                fontSize = 11.sp,
                                color = SaffronOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = horoscope.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = StarlightWhite,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // 4. Four Core Spheres (Love, Career, Wealth, Health)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Cosmic Spheres Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    color = VedicGoldLight
                )

                val categories = listOf(horoscope.love, horoscope.career, horoscope.wealth, horoscope.health)
                categories.forEach { cat ->
                    Surface(
                        color = CosmicCardElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (cat.title) {
                                        horoscope.love.title -> Icons.Default.Favorite
                                        horoscope.career.title -> Icons.Default.Work
                                        horoscope.wealth.title -> Icons.Default.AccountBalance
                                        else -> Icons.Default.FitnessCenter
                                    }
                                    val iconColor = when (cat.title) {
                                        horoscope.love.title -> MysticRuby
                                        horoscope.career.title -> CelestialCyan
                                        horoscope.wealth.title -> VedicGold
                                        else -> AuspiciousGreen
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = cat.title,
                                        tint = iconColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = StarlightWhite
                                    )
                                }

                                // Stars
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    (1..5).forEach { star ->
                                        Icon(
                                            imageVector = if (star <= cat.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = null,
                                            tint = if (star <= cat.rating) VedicGold else StarlightMuted.copy(alpha = 0.4f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = cat.forecast,
                                style = MaterialTheme.typography.bodySmall,
                                color = StarlightMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Auspicious Elements Grid (Lucky Number, Color, Gemstone, Time)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Auspicious Influences Today",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGoldLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Lucky No.", fontSize = 11.sp, color = StarlightMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${horoscope.luckyNumber}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = VedicGold
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Lucky Color", fontSize = 11.sp, color = StarlightMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = horoscope.luckyColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StarlightWhite
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Shubh Muhurat", fontSize = 11.sp, color = StarlightMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = horoscope.luckyTime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AuspiciousGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CosmicOutline)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prescribed Gemstone:",
                            style = MaterialTheme.typography.bodySmall,
                            color = StarlightMuted
                        )
                        Text(
                            text = selectedRashi.luckyGemstone,
                            style = MaterialTheme.typography.labelMedium,
                            color = VedicGoldLight
                        )
                    }
                }
            }
        }

        // 6. Daily Vedic Mantra & Japa Chant Counter
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VedicGoldContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DAILY VEDIC MANTRA",
                        style = MaterialTheme.typography.labelSmall,
                        color = VedicGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = horoscope.dailyMantra,
                        style = MaterialTheme.typography.titleMedium,
                        color = StarlightWhite,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "\"${horoscope.affirmation}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Japa count button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { japaCounter = (japaCounter + 1).coerceAtMost(108) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = VedicGoldLight
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold)
                        ) {
                            Icon(Icons.Default.SelfImprovement, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Chant Japa ($japaCounter / 108)")
                        }
                        if (japaCounter > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { japaCounter = 0 }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset Japa", tint = StarlightMuted)
                            }
                        }
                    }
                }
            }
        }

        // 7. Today's Vedic Panchang Card
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
                            text = "Today's Panchang",
                            style = MaterialTheme.typography.titleMedium,
                            color = VedicGoldLight
                        )
                        Surface(
                            color = SaffronOrange.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = panchang.vaar,
                                fontSize = 11.sp,
                                color = SaffronOrange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val panchangRows = listOf(
                        Pair("Tithi", panchang.tithi),
                        Pair("Nakshatra", panchang.nakshatra),
                        Pair("Yoga", panchang.yoga),
                        Pair("Karana", panchang.karana),
                        Pair("Moon Phase", panchang.moonPhase),
                        Pair("Abhijit Muhurat", panchang.abhijitMuhurat),
                        Pair("Rahu Kaal (Avoid)", panchang.rahuKaal),
                        Pair("Brahma Muhurat", panchang.brahmaMuhurat)
                    )

                    panchangRows.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = StarlightMuted)
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (label.contains("Rahu")) FontWeight.Bold else FontWeight.Medium,
                                color = if (label.contains("Rahu")) CriticalRed else if (label.contains("Abhijit")) AuspiciousGreen else StarlightWhite
                            )
                        }
                    }
                }
            }
        }
    }
}
