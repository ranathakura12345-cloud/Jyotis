package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ScoreGauge(
    score: Double,
    maxScore: Double,
    label: String,
    subLabel: String? = null,
    sizeDp: Dp = 140.dp,
    strokeWidth: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    val progress = (score / maxScore).toFloat().coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val canvasSize = size.minDimension
            val strokePx = strokeWidth.toPx()
            val startAngle = 135f
            val sweepAngle = 270f

            // Background Arc
            drawArc(
                color = CosmicSurfaceVariant,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = Size(canvasSize, canvasSize),
                topLeft = Offset((size.width - canvasSize) / 2f, (size.height - canvasSize) / 2f)
            )

            // Dynamic Gradient based on score tier
            val gradientBrush = when {
                progress >= 0.77f -> Brush.sweepGradient(
                    listOf(VedicGold, AuspiciousGreen, VedicGoldLight)
                )
                progress >= 0.50f -> Brush.sweepGradient(
                    listOf(WarningAmber, VedicGold, VedicGoldLight)
                )
                else -> Brush.sweepGradient(
                    listOf(CriticalRed, SaffronOrange, WarningAmber)
                )
            }

            // Foreground Active Arc
            drawArc(
                brush = gradientBrush,
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedProgress.value,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                size = Size(canvasSize, canvasSize),
                topLeft = Offset((size.width - canvasSize) / 2f, (size.height - canvasSize) / 2f)
            )
        }

        // Inner Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val scoreText = if (score % 1.0 == 0.0) score.toInt().toString() else String.format("%.1f", score)
            Text(
                text = scoreText,
                fontSize = if (sizeDp >= 140.dp) 28.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = VedicGoldLight
            )
            Text(
                text = "/ ${maxScore.toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = StarlightMuted
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = StarlightWhite
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    fontSize = 9.sp,
                    color = StarlightMuted
                )
            }
        }
    }
}
