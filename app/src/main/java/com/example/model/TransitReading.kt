package com.example.model

enum class TransitImpact {
    VERY_FAVORABLE,
    FAVORABLE,
    MIXED_OPPORTUNITY,
    REQUIRES_CAUTION,
    TRANSFORMATIVE
}

enum class SadeSatiPhase(val displayName: String, val description: String) {
    RISING("Rising Phase (Aarohan)", "Saturn transits the 12th house from Moon. Financial caution, spiritual introspection, and increased expenditure."),
    PEAK("Peak Phase (Madhya)", "Saturn transits directly over natal Moon. Mental resilience tested, deep emotional maturation, and restructuring life foundations."),
    SETTING("Setting Phase (Avarohan)", "Saturn transits the 2nd house from Moon. Family harmony, rebuilding finances, and gradual relief."),
    KANTAKA_SHANI("Kantaka / Ashtama Shani (Dhaiya)", "Saturn transits the 4th or 8th house from natal Moon. Career shifts, domestic patience, and health vigilance."),
    NONE("Free from Sade Sati", "No major severe Saturn affliction active from Moon sign. Clear skies for purposeful karmic progress.")
}

data class PlanetaryTransit(
    val planet: Planet,
    val currentRashi: Rashi,
    val isRetrograde: Boolean,
    val transitFromMoon: Int, // 1 to 12th house from user's Moon sign
    val transitFromLagna: Int, // 1 to 12th house from user's Lagna
    val impact: TransitImpact,
    val title: String,
    val description: String,
    val remedies: List<String>
)

data class TransitDashboard(
    val profileName: String,
    val moonRashi: Rashi,
    val lagnaRashi: Rashi,
    val sadeSatiPhase: SadeSatiPhase,
    val sadeSatiAdvice: String,
    val activeTransits: List<PlanetaryTransit>,
    val monthlyCosmicTheme: String,
    val recommendedUpayas: List<String>
)
