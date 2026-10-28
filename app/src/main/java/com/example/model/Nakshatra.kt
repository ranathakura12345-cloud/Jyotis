package com.example.model

enum class Gana(val devanagari: String, val english: String) {
    DEVA("देव", "Divine / Harmonious"),
    MANUSHYA("मनुष्य", "Human / Balanced"),
    RAKSHASA("राक्षस", "Fierce / Dynamic")
}

enum class Nadi(val devanagari: String, val element: String) {
    ADI("आदि", "Vata / Nerve Energy"),
    MADHYA("मध्य", "Pitta / Metabolic Fire"),
    ANTYA("अन्त्य", "Kapha / Physical Constitution")
}

enum class Yoni(val devanagari: String, val animal: String, val enemyYoni: String) {
    ASHWA("अश्व", "Horse", "Mahisha"),
    GAJA("गज", "Elephant", "Simha"),
    MESHA("मेष", "Ram", "Vanara"),
    SARPA("सर्प", "Serpent", "Nakula"),
    SHWANA("श्वान", "Dog", "Mriga"),
    MARJARA("मार्जार", "Cat", "Mushaka"),
    MUSHAKA("मूषक", "Rat", "Marjara"),
    GAU("गौ", "Cow", "Vyaghra"),
    MAHISHA("महिष", "Buffalo", "Ashwa"),
    VYAGHRA("व्याघ्र", "Tiger", "Gau"),
    MRIGA("मृग", "Deer", "Shwana"),
    VANARA("वानर", "Monkey", "Mesha"),
    NAKULA("नकुल", "Mongoose", "Sarpa"),
    SIMHA("सिंह", "Lion", "Gaja")
}

enum class Nakshatra(
    val index: Int, // 1-27
    val sanskritName: String,
    val devanagari: String,
    val rulingPlanet: String,
    val deity: String,
    val gana: Gana,
    val yoni: Yoni,
    val nadi: Nadi,
    val rashiRange: String
) {
    ASHWINI(1, "Ashwini", "अश्विनी", "Ketu", "Ashwini Kumaras", Gana.DEVA, Yoni.ASHWA, Nadi.ADI, "Aries 0° - 13°20'"),
    BHARANI(2, "Bharani", "भरणी", "Shukra", "Yama", Gana.MANUSHYA, Yoni.GAJA, Nadi.MADHYA, "Aries 13°20' - 26°40'"),
    KRITTIKA(3, "Krittika", "कृत्तिका", "Surya", "Agni", Gana.RAKSHASA, Yoni.MESHA, Nadi.ANTYA, "Aries 26°40' - Taurus 10°"),
    ROHINI(4, "Rohini", "रोहिणी", "Chandra", "Brahma", Gana.MANUSHYA, Yoni.SARPA, Nadi.ANTYA, "Taurus 10° - 23°20'"),
    MRIGASHIRA(5, "Mrigashira", "मृगशिरा", "Mangala", "Soma", Gana.DEVA, Yoni.SARPA, Nadi.MADHYA, "Taurus 23°20' - Gemini 6°40'"),
    ARDRA(6, "Ardra", "आर्द्रा", "Rahu", "Rudra", Gana.MANUSHYA, Yoni.SHWANA, Nadi.ADI, "Gemini 6°40' - 20°"),
    PUNARVASU(7, "Punarvasu", "पुनर्वसु", "Guru", "Aditi", Gana.DEVA, Yoni.MARJARA, Nadi.ADI, "Gemini 20° - Cancer 3°20'"),
    PUSHYA(8, "Pushya", "पुष्य", "Shani", "Brihaspati", Gana.DEVA, Yoni.MESHA, Nadi.MADHYA, "Cancer 3°20' - 16°40'"),
    ASHLESHA(9, "Ashlesha", "आश्लेषा", "Budha", "Sarpa", Gana.RAKSHASA, Yoni.MARJARA, Nadi.ANTYA, "Cancer 16°40' - 30°"),
    MAGHA(10, "Magha", "मघा", "Ketu", "Pitris", Gana.RAKSHASA, Yoni.MUSHAKA, Nadi.ANTYA, "Leo 0° - 13°20'"),
    PURVA_PHALGUNI(11, "Purva Phalguni", "पूर्वाफाल्गुनी", "Shukra", "Bhaga", Gana.MANUSHYA, Yoni.MUSHAKA, Nadi.MADHYA, "Leo 13°20' - 26°40'"),
    UTTARA_PHALGUNI(12, "Uttara Phalguni", "उत्तराफाल्गुनी", "Surya", "Aryaman", Gana.MANUSHYA, Yoni.GAU, Nadi.ADI, "Leo 26°40' - Virgo 10°"),
    HASTA(13, "Hasta", "हस्त", "Chandra", "Savitri", Gana.DEVA, Yoni.MAHISHA, Nadi.ADI, "Virgo 10° - 23°20'"),
    CHITRA(14, "Chitra", "चित्रा", "Mangala", "Tvashtar", Gana.RAKSHASA, Yoni.VYAGHRA, Nadi.MADHYA, "Virgo 23°20' - Libra 6°40'"),
    SWATI(15, "Swati", "स्वाति", "Rahu", "Vayu", Gana.DEVA, Yoni.MAHISHA, Nadi.ANTYA, "Libra 6°40' - 20°"),
    VISHAKHA(16, "Vishakha", "विशाखा", "Guru", "Indragni", Gana.RAKSHASA, Yoni.VYAGHRA, Nadi.ANTYA, "Libra 20° - Scorpio 3°20'"),
    ANURADHA(17, "Anuradha", "अनुराधा", "Shani", "Mitra", Gana.DEVA, Yoni.MRIGA, Nadi.MADHYA, "Scorpio 3°20' - 16°40'"),
    JYESHTHA(18, "Jyeshtha", "ज्येष्ठा", "Budha", "Indra", Gana.RAKSHASA, Yoni.MRIGA, Nadi.ADI, "Scorpio 16°40' - 30°"),
    MULA(19, "Mula", "मूल", "Ketu", "Nirriti", Gana.RAKSHASA, Yoni.SHWANA, Nadi.ADI, "Sagittarius 0° - 13°20'"),
    PURVA_ASHADHA(20, "Purva Ashadha", "पूर्वाषाढ़ा", "Shukra", "Apas", Gana.MANUSHYA, Yoni.VANARA, Nadi.MADHYA, "Sagittarius 13°20' - 26°40'"),
    UTTARA_ASHADHA(21, "Uttara Ashadha", "उत्तराषाढ़ा", "Surya", "Vishvadevas", Gana.MANUSHYA, Yoni.NAKULA, Nadi.ANTYA, "Sagittarius 26°40' - Capricorn 10°"),
    SHRAVANA(22, "Shravana", "श्रवण", "Chandra", "Vishnu", Gana.DEVA, Yoni.VANARA, Nadi.ANTYA, "Capricorn 10° - 23°20'"),
    DHANISHTA(23, "Dhanishta", "धनिष्ठा", "Mangala", "Ashta Vasus", Gana.RAKSHASA, Yoni.SIMHA, Nadi.MADHYA, "Capricorn 23°20' - Aquarius 6°40'"),
    SHATABHISHA(24, "Shatabhisha", "शतभिषा", "Rahu", "Varuna", Gana.RAKSHASA, Yoni.ASHWA, Nadi.ADI, "Aquarius 6°40' - 20°"),
    PURVA_BHADRAPADA(25, "Purva Bhadrapada", "पूर्वभाद्रपदा", "Guru", "Aja Ekapada", Gana.MANUSHYA, Yoni.SIMHA, Nadi.ADI, "Aquarius 20° - Pisces 3°20'"),
    UTTARA_BHADRAPADA(26, "Uttara Bhadrapada", "उत्तरभाद्रपदा", "Shani", "Ahirbudhnya", Gana.MANUSHYA, Yoni.GAU, Nadi.MADHYA, "Pisces 3°20' - 16°40'"),
    REVATI(27, "Revati", "रेवती", "Budha", "Pushan", Gana.DEVA, Yoni.GAJA, Nadi.ANTYA, "Pisces 16°40' - 30°");

    companion object {
        fun fromIndex(index: Int): Nakshatra {
            val normalized = ((index - 1) % 27 + 27) % 27 + 1
            return entries.firstOrNull { it.index == normalized } ?: ASHWINI
        }

        fun fromName(name: String): Nakshatra {
            return entries.firstOrNull {
                it.name.equals(name, ignoreCase = true) ||
                it.sanskritName.equals(name, ignoreCase = true)
            } ?: ASHWINI
        }
    }
}
