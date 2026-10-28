package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KundaliChartData
import com.example.model.Rashi
import com.example.ui.theme.*

/**
 * Traditional South Indian Vedic Kundali (Fixed Sign Grid).
 * Outer 4x4 grid where perimeter cells represent the 12 signs:
 * Row 0: Meena (12), Mesha (1), Vrishabha (2), Mithuna (3)
 * Right col: Karka (4), Simha (5)
 * Row 3: Vrishchika (8), Tula (7), Kanya (6) (Right to left)
 * Left col: Dhanu (9), Makara (10), Kumbha (11)
 * Center 2x2 is empty or displays Chart Info.
 */
@Composable
fun SouthIndianKundaliCanvas(
    chartData: KundaliChartData,
    selectedRashi: Rashi? = null,
    onRashiSelected: (Rashi) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .background(CosmicSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(1.5.dp, VedicGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val totalWidth = maxWidth

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / 4f
            val cellH = size.height / 4f
            val stroke = Stroke(width = 2.dp.toPx())
            val gold = VedicGold.copy(alpha = 0.85f)

            // Outer border
            drawRect(color = gold, topLeft = Offset(0f, 0f), size = size, style = stroke)

            // 4x4 Grid lines
            for (i in 1..3) {
                // Horizontal lines
                drawLine(
                    color = gold,
                    start = Offset(0f, i * cellH),
                    end = Offset(size.width, i * cellH),
                    strokeWidth = stroke.width
                )
                // Vertical lines
                drawLine(
                    color = gold,
                    start = Offset(i * cellW, 0f),
                    end = Offset(i * cellW, size.height),
                    strokeWidth = stroke.width
                )
            }

            // Fill center 2x2 with dark mask
            drawRect(
                color = CosmicBackground.copy(alpha = 0.95f),
                topLeft = Offset(cellW, cellH),
                size = Size(cellW * 2, cellH * 2)
            )
        }

        // Map grid cells (row, col) to fixed Rashis
        // [0,0]=Meena, [0,1]=Mesha, [0,2]=Vrishabha, [0,3]=Mithuna
        // [1,3]=Karka, [2,3]=Simha, [3,3]=Kanya, [3,2]=Tula
        // [3,1]=Vrishchika, [3,0]=Dhanu, [2,0]=Makara, [1,0]=Kumbha
        val signMap = listOf(
            Triple(0, 0, Rashi.MEENA),
            Triple(0, 1, Rashi.MESHA),
            Triple(0, 2, Rashi.VRISHABHA),
            Triple(0, 3, Rashi.MITHUNA),
            Triple(1, 3, Rashi.KARKA),
            Triple(2, 3, Rashi.SIMHA),
            Triple(3, 3, Rashi.KANYA),
            Triple(3, 2, Rashi.TULA),
            Triple(3, 1, Rashi.VRISHCHIKA),
            Triple(3, 0, Rashi.DHANU),
            Triple(2, 0, Rashi.MAKARA),
            Triple(1, 0, Rashi.KUMBHA)
        )

        val cellDp = totalWidth / 4f

        signMap.forEach { (row, col, rashi) ->
            val isLagna = rashi == chartData.lagnaRashi
            val isMoon = rashi == chartData.chandraRashi
            val isSelected = selectedRashi == rashi
            val planetsInSign = chartData.planetPlacements.filter { it.rashi == rashi }

            Box(
                modifier = Modifier
                    .offset(
                        x = (col * cellDp.value - (totalWidth.value / 2f) + (cellDp.value / 2f)).dp,
                        y = (row * cellDp.value - (totalWidth.value / 2f) + (cellDp.value / 2f)).dp
                    )
                    .size(cellDp)
                    .clickable { onRashiSelected(rashi) }
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = rashi.sanskritName.take(3).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLagna) VedicGold else StarlightMuted
                    )

                    if (isLagna) {
                        Text(
                            text = "ASC",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaffronOrange
                        )
                    }

                    val planetCodes = planetsInSign.joinToString(" ") { it.planet.code }
                    if (planetCodes.isNotEmpty()) {
                        Text(
                            text = planetCodes,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = VedicGoldLight,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Center Box Text (Lagna & Chandra info)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "LAGNA KUNDALI",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VedicGold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${chartData.lagnaRashi.sanskritName} Lagna",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = StarlightWhite
            )
            Text(
                text = "${chartData.chandraRashi.sanskritName} Rashi",
                fontSize = 10.sp,
                color = StarlightMuted
            )
        }
    }
}
