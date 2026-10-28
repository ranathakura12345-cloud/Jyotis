package com.example.model

data class KootaScore(
    val name: String,
    val devanagari: String,
    val pointsObtained: Double,
    val maxPoints: Double,
    val attributeCompared: String,
    val status: String,
    val explanation: String,
    val remedyIfLow: String? = null
)

enum class MatchingGrade(val title: String, val verdict: String, val ratingColorHex: Long) {
    EXCELLENT("Uttam (उत्तम)", "Highly Auspicious Match with deep spiritual, emotional, and physical harmony.", 0xFF4CAF50),
    GOOD("Madhyam (मध्यम)", "Auspicious & Harmonious Union. Minor differences easily resolved through mutual respect.", 0xFF8BC34A),
    AVERAGE("Samanya (सामान्य)", "Average Compatibility. Practical adjustments and respect for differences recommended.", 0xFFFFB300),
    CHALLENGING("Dosha / Pratikula (प्रतिकूल)", "Challenging Planetary Alignment. Astrological remedies (Upayas) and consultations recommended before proceeding.", 0xFFEF5350)
}

data class CompatibilityReport(
    val person1Name: String,
    val person1MoonRashi: Rashi,
    val person1Nakshatra: Nakshatra,
    val person1IsManglik: Boolean,
    val person2Name: String,
    val person2MoonRashi: Rashi,
    val person2Nakshatra: Nakshatra,
    val person2IsManglik: Boolean,
    val totalScore: Double, // out of 36
    val maxScore: Double = 36.0,
    val grade: MatchingGrade,
    val kootas: List<KootaScore>,
    val manglikCompatibility: String,
    val hasNadiDosha: Boolean,
    val hasBhakootDosha: Boolean,
    val relationshipStrengths: List<String>,
    val potentialFrictionAreas: List<String>,
    val suggestedRemedies: List<String>
)
