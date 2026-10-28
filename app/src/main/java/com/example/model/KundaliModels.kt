package com.example.model

enum class ChartStyle {
    NORTH_INDIAN,
    SOUTH_INDIAN
}

enum class Bhava(
    val number: Int,
    val sanskritName: String,
    val devanagari: String,
    val englishName: String,
    val significations: String,
    val karakaPlanet: Planet
) {
    FIRST(1, "Tanu Bhava", "तनु भाव", "1st House (Lagna / Self)", "Physique, vitality, personality, life orientation, appearance", Planet.SURYA),
    SECOND(2, "Dhana Bhava", "धन भाव", "2nd House (Wealth & Family)", "Accumulated wealth, speech, lineage, eating habits, early childhood", Planet.GURU),
    THIRD(3, "Sahaja Bhava", "सहज भाव", "3rd House (Siblings & Valor)", "Courage, initiative, younger siblings, short travels, communication, skills", Planet.MANGALA),
    FOURTH(4, "Sukha Bhava", "सुख भाव", "4th House (Mother & Inner Peace)", "Mother, vehicles, landed property, domestic happiness, emotional sanctuary", Planet.CHANDRA),
    FIFTH(5, "Putra Bhava", "पुत्र भाव", "5th House (Children & Intelligence)", "Purva punya (past merit), intellect, progeny, romance, speculation, mantras", Planet.GURU),
    SIXTH(6, "Ari / Shatru Bhava", "शत्रु भाव", "6th House (Debts & Disease)", "Health challenges, service, daily work, litigation, enemies, resilience", Planet.MANGALA),
    SEVENTH(7, "Yuvati Bhava", "युवति भाव", "7th House (Partnership & Marriage)", "Spouse, marriage, business partnerships, public relations, foreign residence", Planet.SHUKRA),
    EIGHTH(8, "Randhra Bhava", "रन्ध्र भाव", "8th House (Longevity & Mystery)", "Ayush (longevity), sudden transformations, unearned wealth, occult, secrets", Planet.SHANI),
    NINTH(9, "Dharma Bhava", "धर्म भाव", "9th House (Fortune & Higher Wisdom)", "Bhagya (fortune), father, Guru, pilgrimage, philosophy, higher knowledge", Planet.GURU),
    TENTH(10, "Karma Bhava", "कर्म भाव", "10th House (Profession & Status)", "Career, reputation, leadership, government, social status, accomplishments", Planet.BUDHA),
    ELEVENTH(11, "Labha Bhava", "लाभ भाव", "11th House (Gains & Aspirations)", "Income, profits, elder siblings, network circles, manifestation of desires", Planet.GURU),
    TWELFTH(12, "Vyaya Bhava", "व्यय भाव", "12th House (Loss & Liberation)", "Expenditure, subconscious, foreign lands, isolation, sleep, Moksha (liberation)", Planet.SHANI)
}

enum class Dignity(val label: String) {
    EXALTED("Uchha (Exalted)"),
    OWN("Swakshetra (Own Sign)"),
    FRIENDLY("Mitra (Friendly)"),
    NEUTRAL("Sama (Neutral)"),
    ENEMY("Shatru (Enemy)"),
    DEBILITATED("Neecha (Debilitated)")
}

data class PlanetPlacement(
    val planet: Planet,
    val rashi: Rashi,
    val houseNumber: Int, // 1 to 12
    val degreeInSign: Double,
    val isRetrograde: Boolean = false,
    val dignity: Dignity = Dignity.NEUTRAL
)

data class HouseDetail(
    val bhava: Bhava,
    val rashi: Rashi,
    val planets: List<PlanetPlacement>
)

data class DashaPeriod(
    val mahadasha: Planet,
    val antardasha: Planet,
    val startsYear: Int,
    val endsYear: Int,
    val effectSummary: String
)

data class ManglikStatus(
    val isManglik: Boolean,
    val marsHouse: Int,
    val severity: String,
    val isCancelled: Boolean,
    val explanation: String
)

data class KundaliChartData(
    val profileName: String,
    val birthDate: String,
    val birthTime: String,
    val birthPlace: String,
    val lagnaRashi: Rashi,
    val chandraRashi: Rashi,
    val suryaRashi: Rashi,
    val nakshatra: Nakshatra,
    val nakshatraPada: Int,
    val houses: List<HouseDetail>,
    val planetPlacements: List<PlanetPlacement>,
    val currentDasha: DashaPeriod,
    val manglikStatus: ManglikStatus
)
