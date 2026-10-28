package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HouseDetail
import com.example.model.KundaliChartData
import com.example.ui.theme.*

/**
 * Traditional North Indian Vedic Kundali (Diamond Chart).
 * Outer boundary is a square. Inside it is a diamond.
 * Two diagonal lines cross from corner to corner.
 * House 1 is the top-center diamond (Lagna).
 */
@Composable
fun NorthIndianKundaliCanvas(
    chartData: KundaliChartData,
    selectedHouse: Int? = null,
    onHouseSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .background(CosmicSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(1.5.dp, VedicGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        val sizeDp = maxWidth

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val primaryLineColor = VedicGold.copy(alpha = 0.85f)
            val lineStroke = Stroke(width = 3.dp.toPx())
            val thinStroke = Stroke(width = 1.5.dp.toPx())

            // 1. Outer Square
            drawRect(
                color = primaryLineColor,
                topLeft = Offset(0f, 0f),
                size = size,
                style = lineStroke
            )

            // 2. Diagonals (Corner to Corner)
            drawLine(
                color = primaryLineColor,
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = lineStroke.width
            )
            drawLine(
                color = primaryLineColor,
                start = Offset(w, 0f),
                end = Offset(0f, h),
                strokeWidth = lineStroke.width
            )

            // 3. Inner Diamond (Connecting Midpoints of Outer Square)
            val diamondPath = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(w, h / 2f)
                lineTo(w / 2f, h)
                lineTo(0f, h / 2f)
                close()
            }
            drawPath(path = diamondPath, color = primaryLineColor, style = lineStroke)

            // Optional subtle center accent
            drawCircle(
                color = VedicGoldLight.copy(alpha = 0.15f),
                radius = w * 0.08f,
                center = Offset(w / 2f, h / 2f)
            )
        }

        // Overlay House Numbers, Rashi indices, and Planets inside each of the 12 Bhavas
        // We place 12 responsive boxes corresponding to the 12 houses of the North Indian chart
        val houses = chartData.houses

        // Relative coordinates for North Indian chart houses (x, y fractions):
        // House 1 (Lagna): Center Top Diamond
        // House 2: Top Left Triangle
        // House 3: Upper Left Triangle
        // House 4: Left Diamond
        // House 5: Lower Left Triangle
        // House 6: Bottom Left Triangle
        // House 7: Center Bottom Diamond
        // House 8: Bottom Right Triangle
        // House 9: Lower Right Triangle
        // House 10: Right Diamond
        // House 11: Upper Right Triangle
        // House 12: Top Right Triangle
        val housePositions = listOf(
            Pair(0.50f, 0.28f), // House 1 (Lagna)
            Pair(0.25f, 0.12f), // House 2
            Pair(0.12f, 0.25f), // House 3
            Pair(0.28f, 0.50f), // House 4
            Pair(0.12f, 0.75f), // House 5
            Pair(0.25f, 0.88f), // House 6
            Pair(0.50f, 0.72f), // House 7
            Pair(0.75f, 0.88f), // House 8
            Pair(0.88f, 0.75f), // House 9
            Pair(0.72f, 0.50f), // House 10
            Pair(0.88f, 0.25f), // House 11
            Pair(0.75f, 0.12f)  // House 12
        )

        housePositions.forEachIndexed { index, (relX, relY) ->
            val houseNum = index + 1
            val house = houses.getOrNull(index)
            val isSelected = selectedHouse == houseNum

            val offsetX = (relX - 0.5f) * (sizeDp.value - 24)
            val offsetY = (relY - 0.5f) * (sizeDp.value - 24)

            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp, y = offsetY.dp)
                    .size(if (houseNum in listOf(1, 4, 7, 10)) 68.dp else 56.dp)
                    .background(
                        if (isSelected) VedicGoldContainer.copy(alpha = 0.8f) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onHouseSelected(houseNum) }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Rashi number (1-12)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${house?.rashi?.index ?: houseNum}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (houseNum == 1) VedicGoldLight else StarlightMuted
                        )
                        if (houseNum == 1) {
                            Text(
                                text = " (Asc)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = VedicGold
                            )
                        }
                    }

                    // Planets in this house
                    val planetCodes = house?.planets?.joinToString(" ") { it.planet.code } ?: ""
                    if (planetCodes.isNotEmpty()) {
                        Text(
                            text = planetCodes,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VedicGoldLight,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
