package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun CosmicBackground(
    content: @Composable () -> Unit
) {
    // Generate deterministic constellation stars
    val stars = remember {
        val rand = Random(42)
        List(40) {
            Triple(
                rand.nextFloat(), // x fraction 0..1
                rand.nextFloat(), // y fraction 0..1
                rand.nextFloat() * 1.8f + 0.6f // radius
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background deep space gradient
            val radialGlow = Brush.radialGradient(
                colors = listOf(
                    CosmicCardElevated.copy(alpha = 0.45f),
                    CosmicSurface.copy(alpha = 0.8f),
                    CosmicBackground
                ),
                center = Offset(size.width * 0.5f, size.height * 0.15f),
                radius = size.width * 0.9f
            )
            drawRect(brush = radialGlow)

            // Draw subtle twinkling stars
            stars.forEach { (xFrac, yFrac, radius) ->
                val px = xFrac * size.width
                val py = yFrac * size.height
                drawCircle(
                    color = StarlightWhite.copy(alpha = (radius / 3.0f).coerceIn(0.15f, 0.7f)),
                    radius = radius,
                    center = Offset(px, py)
                )
            }
        }

        content()
    }
}
