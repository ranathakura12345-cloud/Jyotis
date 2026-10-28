package com.example.model

enum class Element(val sanskrit: String, val english: String) {
    AGNI("अग्नि", "Fire"),
    PRITHVI("पृथ्वी", "Earth"),
    VAYU("वायु", "Air"),
    JALA("जल", "Water")
}

enum class Modality(val sanskrit: String, val english: String) {
    CHARA("चर", "Movable"),
    STHIRA("स्थिर", "Fixed"),
    DWISVABHAVA("द्विस्वभाव", "Dual")
}

enum class Rashi(
    val index: Int, // 1-12
    val sanskritName: String,
    val devanagari: String,
    val englishName: String,
    val lordSanskrit: String,
    val lordEnglish: String,
    val element: Element,
    val modality: Modality,
    val symbol: String,
    val glyph: String,
    val luckyNumbers: List<Int>,
    val luckyColor: String,
    val luckyGemstone: String,
    val qualities: String
) {
    MESHA(
        1, "Mesha", "मेष", "Aries", "Mangala", "Mars",
        Element.AGNI, Modality.CHARA, "Ram", "♈",
        listOf(1, 8, 9), "Crimson Red", "Red Coral (Moonga)",
        "Courageous, energetic, ambitious, pioneering, natural leader"
    ),
    VRISHABHA(
        2, "Vrishabha", "वृषभ", "Taurus", "Shukra", "Venus",
        Element.PRITHVI, Modality.STHIRA, "Bull", "♉",
        listOf(2, 6, 7), "Lotus Pink & White", "Diamond / Opal",
        "Steadfast, patient, artistic, grounded, prosperous"
    ),
    MITHUNA(
        3, "Mithuna", "मिथुन", "Gemini", "Budha", "Mercury",
        Element.VAYU, Modality.DWISVABHAVA, "Twins", "♊",
        listOf(3, 5, 6), "Emerald Green", "Emerald (Panna)",
        "Intellectual, witty, adaptable, communicative, curious"
    ),
    KARKA(
        4, "Karka", "कर्क", "Cancer", "Chandra", "Moon",
        Element.JALA, Modality.CHARA, "Crab", "♋",
        listOf(2, 4, 7), "Pearl White & Silver", "Pearl (Moti)",
        "Intuitive, nurturing, empathetic, protective, deeply devoted"
    ),
    SIMHA(
        5, "Simha", "सिंह", "Leo", "Surya", "Sun",
        Element.AGNI, Modality.STHIRA, "Lion", "♌",
        listOf(1, 4, 9), "Royal Gold & Saffron", "Ruby (Manikya)",
        "Regal, radiant, generous, self-confident, charismatic"
    ),
    KANYA(
        6, "Kanya", "कन्या", "Virgo", "Budha", "Mercury",
        Element.PRITHVI, Modality.DWISVABHAVA, "Maiden", "♍",
        listOf(3, 5, 8), "Forest Green & Tan", "Emerald (Panna)",
        "Analytical, meticulous, healing, service-oriented, discerning"
    ),
    TULA(
        7, "Tula", "तुला", "Libra", "Shukra", "Venus",
        Element.VAYU, Modality.CHARA, "Scales", "♎",
        listOf(2, 6, 7), "Pastel Blue & White", "White Sapphire / Diamond",
        "Harmonious, diplomatic, just, aesthetic, romantic"
    ),
    VRISHCHIKA(
        8, "Vrishchika", "वृश्चिक", "Scorpio", "Mangala", "Mars",
        Element.JALA, Modality.STHIRA, "Scorpion", "♏",
        listOf(1, 4, 9), "Maroon & Deep Rust", "Red Coral (Moonga)",
        "Intense, transformative, magnetic, occult seeker, deeply resilient"
    ),
    DHANU(
        9, "Dhanu", "धनु", "Sagittarius", "Guru", "Jupiter",
        Element.AGNI, Modality.DWISVABHAVA, "Archer", "♐",
        listOf(3, 7, 9), "Bright Yellow & Golden", "Yellow Sapphire (Pukhraj)",
        "Philosophical, optimistic, truth seeker, expansive, jovial"
    ),
    MAKARA(
        10, "Makara", "मकर", "Capricorn", "Shani", "Saturn",
        Element.PRITHVI, Modality.CHARA, "Sea Goat", "♑",
        listOf(4, 8, 13), "Deep Navy & Charcoal", "Blue Sapphire (Neelam)",
        "Disciplined, pragmatic, persevering, karmic builder, authoritative"
    ),
    KUMBHA(
        11, "Kumbha", "कुम्भ", "Aquarius", "Shani", "Saturn",
        Element.VAYU, Modality.STHIRA, "Water Bearer", "♒",
        listOf(4, 8, 11), "Electric Blue & Violet", "Blue Sapphire / Amethyst",
        "Visionary, humanitarian, unconventional, detached intellect"
    ),
    MEENA(
        12, "Meena", "मीन", "Pisces", "Guru", "Jupiter",
        Element.JALA, Modality.DWISVABHAVA, "Two Fishes", "♓",
        listOf(3, 7, 12), "Sea Green & Pale Gold", "Yellow Sapphire (Pukhraj)",
        "Mystical, compassionate, artistic, spiritual transcendence"
    );

    companion object {
        fun fromIndex(index: Int): Rashi {
            val normalized = ((index - 1) % 12 + 12) % 12 + 1
            return entries.firstOrNull { it.index == normalized } ?: MESHA
        }

        fun fromName(name: String): Rashi {
            return entries.firstOrNull {
                it.name.equals(name, ignoreCase = true) ||
                it.sanskritName.equals(name, ignoreCase = true) ||
                it.englishName.equals(name, ignoreCase = true)
            } ?: MESHA
        }
    }
}
