package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VedicGold,
    onPrimary = Color(0xFF221600),
    primaryContainer = VedicGoldContainer,
    onPrimaryContainer = VedicGoldLight,
    secondary = SaffronOrange,
    onSecondary = Color(0xFF290E00),
    secondaryContainer = Color(0xFF4A1E0B),
    onSecondaryContainer = Color(0xFFFFCCBC),
    tertiary = CelestialCyan,
    onTertiary = Color(0xFF00363D),
    tertiaryContainer = Color(0xFF004F58),
    onTertiaryContainer = Color(0xFFB2EBF2),
    background = CosmicBackground,
    onBackground = StarlightWhite,
    surface = CosmicSurface,
    onSurface = StarlightWhite,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = StarlightMuted,
    outline = CosmicOutline,
    outlineVariant = CosmicOutlineVariant
)

private val LightColorScheme = darkColorScheme(
    // We maintain a dignified dark celestial atmosphere for Vedic Astrology
    primary = VedicGold,
    onPrimary = Color(0xFF221600),
    primaryContainer = VedicGoldContainer,
    onPrimaryContainer = VedicGoldLight,
    secondary = SaffronOrange,
    onSecondary = Color(0xFF290E00),
    secondaryContainer = Color(0xFF4A1E0B),
    onSecondaryContainer = Color(0xFFFFCCBC),
    tertiary = CelestialCyan,
    onTertiary = Color(0xFF00363D),
    background = CosmicBackground,
    onBackground = StarlightWhite,
    surface = CosmicSurface,
    onSurface = StarlightWhite,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = StarlightMuted,
    outline = CosmicOutline
)

@Composable
fun JyotishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
