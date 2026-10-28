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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun TransitsScreen(
    activeProfile: UserProfileEntity?,
    getTransits: (UserProfileEntity) -> TransitDashboard,
    onOpenProfileManager: () -> Unit
) {
    if (activeProfile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Please select or create a profile to calculate personalized transits.", color = StarlightMuted)
        }
        return
    }

    val dashboard = remember(activeProfile) { getTransits(activeProfile) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // 1. Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Personalized Gochar",
                        style = MaterialTheme.typography.headlineMedium,
                        color = VedicGoldLight
                    )
                    Text(
                        text = "Transits calculated for ${dashboard.moonRashi.sanskritName} Moon & ${dashboard.lagnaRashi.sanskritName} Lagna",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted
                    )
                }

                Surface(
                    onClick = onOpenProfileManager,
                    color = CosmicSurfaceVariant,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = dashboard.profileName,
                        style = MaterialTheme.typography.labelMedium,
                        color = StarlightWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 2. Cosmic Transit Theme Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CosmicOutline)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessMedium,
                        contentDescription = null,
                        tint = VedicGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Current Planetary Alignments",
                            style = MaterialTheme.typography.titleSmall,
                            color = VedicGoldLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dashboard.monthlyCosmicTheme,
                            style = MaterialTheme.typography.bodySmall,
                            color = StarlightWhite,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // 3. Sade Sati / Dhaiya Status Card
        item {
            val phase = dashboard.sadeSatiPhase
            val (statusColor, badgeText) = when (phase) {
                SadeSatiPhase.NONE -> Pair(AuspiciousGreen, "Sade Sati Free")
                SadeSatiPhase.RISING -> Pair(WarningAmber, "Rising Phase")
                SadeSatiPhase.PEAK -> Pair(CriticalRed, "Peak Phase (Janma Shani)")
                SadeSatiPhase.SETTING -> Pair(WarningAmber, "Setting Phase")
                SadeSatiPhase.KANTAKA_SHANI -> Pair(SaffronOrange, "Dhaiya Active")
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicCardElevated),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Shani Sade Sati Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = StarlightWhite
                            )
                        }

                        Surface(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = phase.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightWhite,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = dashboard.sadeSatiAdvice,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 4. Active Planetary Transits List
        item {
            Text(
                text = "Major Active Planetary Transits (Gochar)",
                style = MaterialTheme.typography.titleMedium,
                color = VedicGoldLight
            )
        }

        items(dashboard.activeTransits) { transit ->
            val impactColor = when (transit.impact) {
                TransitImpact.VERY_FAVORABLE -> AuspiciousGreen
                TransitImpact.FAVORABLE -> AuspiciousGreen
                TransitImpact.MIXED_OPPORTUNITY -> WarningAmber
                TransitImpact.REQUIRES_CAUTION -> CriticalRed
                TransitImpact.TRANSFORMATIVE -> CelestialPurple
            }

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = transit.planet.symbol,
                                fontSize = 20.sp,
                                color = VedicGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${transit.planet.sanskritName} in ${transit.currentRashi.sanskritName}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = StarlightWhite
                                )
                                Text(
                                    text = "House ${transit.transitFromMoon} from Moon • House ${transit.transitFromLagna} from Lagna",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StarlightMuted
                                )
                            }
                        }

                        Surface(
                            color = impactColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = transit.impact.name.replace('_', ' '),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = impactColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = transit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightWhite,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Vedic Remedies (Upayas):",
                        style = MaterialTheme.typography.labelSmall,
                        color = VedicGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    transit.remedies.forEach { remedy ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = VedicGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = remedy,
                                style = MaterialTheme.typography.bodySmall,
                                color = StarlightMuted
                            )
                        }
                    }
                }
            }
        }

        // 5. Daily Recommended Upayas & Rituals
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = VedicGoldContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "General Vedic Spiritual Upayas",
                        style = MaterialTheme.typography.titleSmall,
                        color = VedicGoldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    dashboard.recommendedUpayas.forEach { upaya ->
                        Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                            Text("✦ ", color = VedicGold, fontSize = 14.sp)
                            Text(
                                text = upaya,
                                style = MaterialTheme.typography.bodySmall,
                                color = StarlightWhite
                            )
                        }
                    }
                }
            }
        }
    }
}
