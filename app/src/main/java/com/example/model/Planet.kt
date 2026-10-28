package com.example.model

enum class GrahaNature(val english: String) {
    BENEFIC("Saumya / Benefic"),
    MALEFIC("Kroora / Natural Malefic"),
    NEUTRAL("Mishra / Neutral & Adaptive")
}

enum class Planet(
    val code: String,
    val sanskritName: String,
    val devanagari: String,
    val englishName: String,
    val symbol: String,
    val nature: GrahaNature,
    val beejMantra: String,
    val gayatriMantra: String,
    val signifies: String,
    val exaltationRashi: Rashi,
    val debilitationRashi: Rashi,
    val ownRashis: List<Rashi>,
    val dashaYears: Int
) {
    SURYA(
        "Su", "Surya", "सूर्य", "Sun", "☉",
        GrahaNature.MALEFIC,
        "ॐ ह्रां ह्रीं ह्रौं सः सूर्याय नमः",
        "ॐ भास्कराय विद्महे महाद्युतिकराय धीमहि तन्नो आदित्यः प्रचोदयात्",
        "Soul (Atman), vitality, father, government, authority, leadership",
        Rashi.MESHA, Rashi.TULA, listOf(Rashi.SIMHA), 6
    ),
    CHANDRA(
        "Ch", "Chandra", "चन्द्र", "Moon", "☽",
        GrahaNature.BENEFIC,
        "ॐ श्रां श्रीं श्रौं सः चन्द्रमसे नमः",
        "ॐ क्षीरपुत्राय विद्महे अमृतत्त्वाय धीमहि तन्नो सोमः प्रचोदयात्",
        "Mind (Manas), emotions, mother, mental peace, fluids, public popularity",
        Rashi.VRISHABHA, Rashi.VRISHCHIKA, listOf(Rashi.KARKA), 10
    ),
    MANGALA(
        "Ma", "Mangala", "मङ्गल", "Mars", "♂",
        GrahaNature.MALEFIC,
        "ॐ क्रां क्रीं क्रौं सः भौमाय नमः",
        "ॐ अङ्गारकाय विद्महे शक्तिहस्ताय धीमहि तन्नो भौमः प्रचोदयात्",
        "Courage, energy, siblings, land/property, ambition, military, technical drive",
        Rashi.MAKARA, Rashi.KARKA, listOf(Rashi.MESHA, Rashi.VRISHCHIKA), 7
    ),
    BUDHA(
        "Bu", "Budha", "बुध", "Mercury", "☿",
        GrahaNature.NEUTRAL,
        "ॐ ब्रां ब्रीं ब्रौं सः बुधाय नमः",
        "ॐ सौम्यरूपाय विद्महे वाणेशाय धीमहि तन्नो सौम्यः प्रचोदयात्",
        "Intellect (Buddhi), speech, business, commerce, analytics, communication",
        Rashi.KANYA, Rashi.MEENA, listOf(Rashi.MITHUNA, Rashi.KANYA), 17
    ),
    GURU(
        "Gu", "Guru / Brihaspati", "गुरु", "Jupiter", "♃",
        GrahaNature.BENEFIC,
        "ॐ ग्रां ग्रीं ग्रौं सः गुरवे नमः",
        "ॐ देवाधिपाय विद्महे देवपूज्याय धीमहि तन्नो गुरुः प्रचोदयात्",
        "Wisdom, guru, dharma, children, wealth, higher philosophy, spiritual grace",
        Rashi.KARKA, Rashi.MAKARA, listOf(Rashi.DHANU, Rashi.MEENA), 16
    ),
    SHUKRA(
        "Sk", "Shukra", "शुक्र", "Venus", "♀",
        GrahaNature.BENEFIC,
        "ॐ द्रां द्रीं द्रौं सः शुक्राय नमः",
        "ॐ भृगुपुत्राय विद्महे दिव्यदेहाय धीमहि तन्नो शुक्रः प्रचोदयात्",
        "Love, marriage, arts, luxury, beauty, vehicles, sensual harmony",
        Rashi.MEENA, Rashi.KANYA, listOf(Rashi.VRISHABHA, Rashi.TULA), 20
    ),
    SHANI(
        "Sa", "Shani", "शनि", "Saturn", "♄",
        GrahaNature.MALEFIC,
        "ॐ प्रां प्रीं प्रौं सः शनैश्चराय नमः",
        "ॐ सूर्यपुत्राय विद्महे मृत्युरूपाय धीमहि तन्नो मन्दः प्रचोदयात्",
        "Karma, discipline, longevity, patience, perseverance, hard work, justice",
        Rashi.TULA, Rashi.MESHA, listOf(Rashi.MAKARA, Rashi.KUMBHA), 19
    ),
    RAHU(
        "Ra", "Rahu", "राहु", "North Node", "☊",
        GrahaNature.MALEFIC,
        "ॐ भ्रां भ्रीं भ्रौं सः राहवे नमः",
        "ॐ नागाधिपाय विद्महे पद्मनेत्राय धीमहि तन्नो राहुः प्रचोदयात्",
        "Ambition, innovation, obsession, worldly desires, sudden events, foreign lands",
        Rashi.VRISHABHA, Rashi.VRISHCHIKA, listOf(Rashi.KUMBHA), 18
    ),
    KETU(
        "Ke", "Ketu", "केतु", "South Node", "☋",
        GrahaNature.MALEFIC,
        "ॐ स्रां स्रीं स्रौं सः केतवे नमः",
        "ॐ अश्मध्वजाय विद्महे शूलहस्ताय धीमहि तन्नो केतुः प्रचोदयात्",
        "Moksha (Liberation), spirituality, detachment, intuition, occult wisdom",
        Rashi.VRISHCHIKA, Rashi.VRISHABHA, listOf(Rashi.VRISHCHIKA), 7
    );

    companion object {
        fun fromCode(code: String): Planet? = entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}
