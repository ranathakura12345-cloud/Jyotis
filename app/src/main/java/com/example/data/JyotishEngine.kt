package com.example.data

import com.example.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.floor

object JyotishEngine {

    // --- Astronomical & Sidereal Calculations ---

    /**
     * Calculates Moon Sign, Ascendant (Lagna), and Nakshatra from Birth details
     */
    fun calculateBirthChart(
        name: String,
        birthDateStr: String,
        birthTimeStr: String,
        birthPlace: String,
        latitude: Double,
        longitude: Double,
        tzOffset: Double
    ): KundaliChartData {
        val date = try {
            LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.of(1995, 5, 15)
        }

        val time = try {
            LocalTime.parse(birthTimeStr, DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            LocalTime.of(12, 0)
        }

        // Epoch reference: Jan 1, 2000 (J2000)
        val epoch = LocalDate.of(2000, 1, 1)
        val daysSinceEpoch = ChronoUnit.DAYS.between(epoch, date).toDouble() +
                (time.hour + time.minute / 60.0 - tzOffset) / 24.0

        // 1. Calculate Mean Sun Longitude in Sidereal Zodiac (Lahiri Ayanamsha ~ 23.85° at J2000)
        val tropicalSun = (280.460 + 0.9856474 * daysSinceEpoch) % 360.0
        val ayanamsha = 23.85 + (daysSinceEpoch / 365.25) * 0.01396 // Precession
        val siderealSunDeg = (tropicalSun - ayanamsha + 360.0) % 360.0
        val sunRashiIndex = (floor(siderealSunDeg / 30.0).toInt() % 12) + 1
        val suryaRashi = Rashi.fromIndex(sunRashiIndex)

        // 2. Calculate Moon Longitude in Sidereal Zodiac (Moon moves ~13.176358° per day)
        val tropicalMoon = (218.316 + 13.176396 * daysSinceEpoch) % 360.0
        val siderealMoonDeg = (tropicalMoon - ayanamsha + 360.0) % 360.0
        val moonRashiIndex = (floor(siderealMoonDeg / 30.0).toInt() % 12) + 1
        val chandraRashi = Rashi.fromIndex(moonRashiIndex)

        // Nakshatra calculation (360° / 27 = 13°20' or 13.3333° per Nakshatra)
        val nakshatraFloat = siderealMoonDeg / (360.0 / 27.0)
        val nakshatraIndex = (floor(nakshatraFloat).toInt() % 27) + 1
        val nakshatra = Nakshatra.fromIndex(nakshatraIndex)
        val pada = (floor((nakshatraFloat - floor(nakshatraFloat)) * 4).toInt() % 4) + 1

        // 3. Calculate Ascendant (Lagna) based on Local Sidereal Time
        // Local Sidereal Time approximation:
        val gmstHours = (18.697374558 + 24.06570982441908 * daysSinceEpoch) % 24.0
        val localSiderealTimeHours = (gmstHours + longitude / 15.0 + 24.0) % 24.0
        val ramcDeg = localSiderealTimeHours * 15.0
        // Obliquity of ecliptic ~ 23.44°
        val lagnaDeg = (ramcDeg - ayanamsha + 360.0) % 360.0
        val lagnaRashiIndex = (floor(lagnaDeg / 30.0).toInt() % 12) + 1
        val lagnaRashi = Rashi.fromIndex(lagnaRashiIndex)

        // 4. Calculate Planet Placements across the 12 Bhavas
        // We calculate relative house positions from Lagna (Equal House system / Bhava Chalita)
        val placements = mutableListOf<PlanetPlacement>()

        fun addPlacement(planet: Planet, baseDeg: Double, dailyRate: Double) {
            val pTropical = (baseDeg + dailyRate * daysSinceEpoch) % 360.0
            val pSidereal = (pTropical - ayanamsha + 360.0) % 360.0
            val rashiIdx = (floor(pSidereal / 30.0).toInt() % 12) + 1
            val planetRashi = Rashi.fromIndex(rashiIdx)
            // House number relative to Lagna: House 1 is Lagna sign
            val houseNum = ((planetRashi.index - lagnaRashi.index + 12) % 12) + 1
            val degInSign = pSidereal % 30.0

            // Determine dignity
            val dignity = when {
                planetRashi == planet.exaltationRashi -> Dignity.EXALTED
                planetRashi == planet.debilitationRashi -> Dignity.DEBILITATED
                planet.ownRashis.contains(planetRashi) -> Dignity.OWN
                else -> Dignity.FRIENDLY
            }

            placements.add(
                PlanetPlacement(
                    planet = planet,
                    rashi = planetRashi,
                    houseNumber = houseNum,
                    degreeInSign = degInSign,
                    isRetrograde = (planet == Planet.RAHU || planet == Planet.KETU),
                    dignity = dignity
                )
            )
        }

        // Add 9 Navagrahas with mean planetary motions:
        addPlacement(Planet.SURYA, 280.460, 0.9856474)
        addPlacement(Planet.CHANDRA, 218.316, 13.176396)
        addPlacement(Planet.MANGALA, 355.433, 0.524033)
        addPlacement(Planet.BUDHA, 252.251, 4.092334)
        addPlacement(Planet.GURU, 34.351, 0.083085)
        addPlacement(Planet.SHUKRA, 181.979, 1.602130)
        addPlacement(Planet.SHANI, 50.077, 0.033444)
        // Rahu moves retrograde ~0.05295 deg/day
        addPlacement(Planet.RAHU, 125.044, -0.052954)
        // Ketu is 180 deg opposite Rahu
        val rahuPlacement = placements.last()
        val ketuRashi = Rashi.fromIndex(((rahuPlacement.rashi.index + 5) % 12) + 1)
        val ketuHouse = ((ketuRashi.index - lagnaRashi.index + 12) % 12) + 1
        placements.add(
            PlanetPlacement(
                planet = Planet.KETU,
                rashi = ketuRashi,
                houseNumber = ketuHouse,
                degreeInSign = rahuPlacement.degreeInSign,
                isRetrograde = true,
                dignity = Dignity.NEUTRAL
            )
        )

        // Construct 12 Houses
        val houses = (1..12).map { houseNum ->
            val signIndex = ((lagnaRashi.index + houseNum - 2) % 12) + 1
            val rashi = Rashi.fromIndex(signIndex)
            val bhava = Bhava.entries[houseNum - 1]
            val planetsInHouse = placements.filter { it.houseNumber == houseNum }
            HouseDetail(bhava, rashi, planetsInHouse)
        }

        // 5. Evaluate Manglik Dosha (Kuja Dosha)
        // Classical check: Mars in 1st, 4th, 7th, 8th, or 12th house from Lagna or Moon
        val marsPlacement = placements.first { it.planet == Planet.MANGALA }
        val marsHouse = marsPlacement.houseNumber
        val isManglikHouse = marsHouse in listOf(1, 4, 7, 8, 12)
        val isCancelled = marsPlacement.rashi in listOf(Rashi.MESHA, Rashi.VRISHCHIKA, Rashi.MAKARA) ||
                placements.any { it.planet == Planet.GURU && (it.houseNumber == marsHouse || abs(it.houseNumber - marsHouse) == 4 || abs(it.houseNumber - marsHouse) == 8) }

        val manglikExplanation = when {
            !isManglikHouse -> "No Manglik Dosha detected. Mars is favorably situated in House $marsHouse (${marsPlacement.rashi.englishName})."
            isCancelled -> "Anshik / Cancelled Manglik Dosha. Mars in House $marsHouse is mitigated because Mars is in ${marsPlacement.rashi.sanskritName} or aspected by auspicious Guru."
            else -> "Full Manglik Dosha (Kuja Dosha) active. Mars resides in House $marsHouse. Marriage with a compatible Manglik partner or performing Kumbh Vivah / Mars remedies recommended."
        }

        val manglikStatus = ManglikStatus(
            isManglik = isManglikHouse,
            marsHouse = marsHouse,
            severity = if (!isManglikHouse) "None" else if (isCancelled) "Mild / Mitigated" else "Significant",
            isCancelled = isCancelled,
            explanation = manglikExplanation
        )

        // 6. Current Vimshottari Mahadasha
        // Nakshatra ruling planet initiates the sequence of 120 years:
        // Ketu(7), Venus(20), Sun(6), Moon(10), Mars(7), Rahu(18), Jupiter(16), Saturn(19), Mercury(17)
        val dashaPlanets = listOf(
            Planet.KETU, Planet.SHUKRA, Planet.SURYA, Planet.CHANDRA,
            Planet.MANGALA, Planet.RAHU, Planet.GURU, Planet.SHANI, Planet.BUDHA
        )
        val startPlanet = when (nakshatra.rulingPlanet) {
            "Ketu" -> Planet.KETU
            "Shukra" -> Planet.SHUKRA
            "Surya" -> Planet.SURYA
            "Chandra" -> Planet.CHANDRA
            "Mangala" -> Planet.MANGALA
            "Rahu" -> Planet.RAHU
            "Guru" -> Planet.GURU
            "Shani" -> Planet.SHANI
            else -> Planet.BUDHA
        }
        val startIdx = dashaPlanets.indexOf(startPlanet)
        val currentYear = LocalDate.now().year
        val birthYear = date.year
        val age = (currentYear - birthYear).coerceAtLeast(0)

        // Cycle through dasha durations
        var elapsed = 0
        var currentMahadasha = startPlanet
        var startDashaYear = birthYear
        var endDashaYear = birthYear + startPlanet.dashaYears

        for (i in 0 until 9) {
            val p = dashaPlanets[(startIdx + i) % 9]
            if (age in elapsed..(elapsed + p.dashaYears)) {
                currentMahadasha = p
                startDashaYear = birthYear + elapsed
                endDashaYear = startDashaYear + p.dashaYears
                break
            }
            elapsed += p.dashaYears
        }

        // Antardasha estimation
        val subIndex = ((age * 2) % 9)
        val antardashaPlanet = dashaPlanets[(dashaPlanets.indexOf(currentMahadasha) + subIndex) % 9]

        val dashaSummary = "Currently traversing the ${currentMahadasha.sanskritName} Mahadasha with ${antardashaPlanet.sanskritName} Antardasha. " +
                "Brings karmic focus onto ${currentMahadasha.signifies.substringBefore(',')} and spiritual maturation."

        val currentDasha = DashaPeriod(
            mahadasha = currentMahadasha,
            antardasha = antardashaPlanet,
            startsYear = startDashaYear,
            endsYear = endDashaYear,
            effectSummary = dashaSummary
        )

        return KundaliChartData(
            profileName = name,
            birthDate = birthDateStr,
            birthTime = birthTimeStr,
            birthPlace = birthPlace,
            lagnaRashi = lagnaRashi,
            chandraRashi = chandraRashi,
            suryaRashi = suryaRashi,
            nakshatra = nakshatra,
            nakshatraPada = pada,
            houses = houses,
            planetPlacements = placements,
            currentDasha = currentDasha,
            manglikStatus = manglikStatus
        )
    }

    // --- Ashtakoota 36-Gun Milan Compatibility Engine ---

    fun calculateCompatibility(
        person1: KundaliChartData,
        person2: KundaliChartData
    ): CompatibilityReport {
        val rashi1 = person1.chandraRashi
        val rashi2 = person2.chandraRashi
        val nak1 = person1.nakshatra
        val nak2 = person2.nakshatra

        // 1. Varna (1 Point) - Spiritual Harmony
        // Brahmin (Water: Cancer, Scorpio, Pisces) = 4
        // Kshatriya (Fire: Aries, Leo, Sag) = 3
        // Vaishya (Earth: Taurus, Virgo, Cap) = 2
        // Shudra (Air: Gemini, Libra, Aquarius) = 1
        fun getVarnaScore(rashi: Rashi): Int = when (rashi.element) {
            Element.JALA -> 4
            Element.AGNI -> 3
            Element.PRITHVI -> 2
            Element.VAYU -> 1
        }
        val varna1 = getVarnaScore(rashi1)
        val varna2 = getVarnaScore(rashi2)
        val varnaPoints = if (varna1 >= varna2) 1.0 else 0.0
        val varnaKoota = KootaScore(
            name = "Varna Koota",
            devanagari = "वर्ण कूट",
            pointsObtained = varnaPoints,
            maxPoints = 1.0,
            attributeCompared = "Spiritual & Ego Compatibility",
            status = if (varnaPoints == 1.0) "Harmonious" else "Mild Friction",
            explanation = if (varnaPoints == 1.0)
                "Spiritual paths and mutual respect are well aligned. No ego conflicts in mutual growth."
            else
                "Differences in ideological outlooks. Nurture conscious active listening and humility."
        )

        // 2. Vashya (2 Points) - Mutual Attraction & Influence
        // Chatushpada (Aries, Taurus, 2nd half Sag, 1st half Cap)
        // Manava (Gemini, Virgo, Libra, 1st half Sag, Aquarius)
        // Jalachara (Cancer, Pisces, 2nd half Cap)
        // Vanachara (Leo)
        // Keeta (Scorpio)
        val vashyaPoints = when {
            rashi1 == rashi2 -> 2.0
            (rashi1.element == rashi2.element) -> 2.0
            (rashi1.element == Element.AGNI && rashi2.element == Element.VAYU) ||
            (rashi1.element == Element.VAYU && rashi2.element == Element.AGNI) -> 1.5
            (rashi1.element == Element.PRITHVI && rashi2.element == Element.JALA) ||
            (rashi1.element == Element.JALA && rashi2.element == Element.PRITHVI) -> 1.5
            else -> 0.5
        }
        val vashyaKoota = KootaScore(
            name = "Vashya Koota",
            devanagari = "वश्य कूट",
            pointsObtained = vashyaPoints,
            maxPoints = 2.0,
            attributeCompared = "Magnetic Attraction & Mutual Control",
            status = if (vashyaPoints >= 1.5) "Magnetic & Balanced" else "Moderate Control Need",
            explanation = if (vashyaPoints >= 1.5)
                "Strong natural affinity and effortless understanding of each other's boundaries."
            else
                "Requires conscious mutual yielding. Neither partner should seek unilateral dominance."
        )

        // 3. Tara (3 Points) - Destiny & Longevity Harmony
        val distance1to2 = ((nak2.index - nak1.index + 27) % 9) + 1
        val distance2to1 = ((nak1.index - nak2.index + 27) % 9) + 1
        val auspiciousTaras = listOf(2, 4, 6, 8, 9) // Sampat, Kshema, Sadhaka, Mitra, Parama Mitra
        val tara1Ok = distance1to2 in auspiciousTaras
        val tara2Ok = distance2to1 in auspiciousTaras
        val taraPoints = when {
            tara1Ok && tara2Ok -> 3.0
            tara1Ok || tara2Ok -> 1.5
            else -> 0.0
        }
        val taraKoota = KootaScore(
            name = "Tara Koota",
            devanagari = "तारा कूट",
            pointsObtained = taraPoints,
            maxPoints = 3.0,
            attributeCompared = "Destiny & Health Compatibility",
            status = if (taraPoints >= 2.0) "Highly Auspicious" else "Neutral / Needs Care",
            explanation = if (taraPoints >= 2.0)
                "Fortuitous destiny alignment. Partnership fosters health, prosperity, and longevity."
            else
                "Minor planetary friction on wellness. Daily Maha Mrityunjaya recitation recommended."
        )

        // 4. Yoni (4 Points) - Intimacy & Instinctual Harmony
        val yoni1 = nak1.yoni
        val yoni2 = nak2.yoni
        val isEnemies = yoni1.enemyYoni == yoni2.name || yoni2.enemyYoni == yoni1.name
        val yoniPoints = when {
            yoni1 == yoni2 -> 4.0
            isEnemies -> 0.0
            else -> 2.5
        }
        val yoniKoota = KootaScore(
            name = "Yoni Koota",
            devanagari = "योनि कूट",
            pointsObtained = yoniPoints,
            maxPoints = 4.0,
            attributeCompared = "Biological & Emotional Affection",
            status = if (yoniPoints >= 3.0) "Passionate & Harmonious" else if (yoniPoints > 0) "Friendly" else "Instinctual Opposition",
            explanation = if (yoniPoints >= 3.0)
                "Exceptional biological and instinctual synergy. Mutual physical and emotional comfort."
            else if (yoniPoints > 0)
                "Warm, companionable intimacy with room for deepening affection over time."
            else
                "Different temperament styles. Patience and gentle emotional reassurance bridge the gap."
        )

        // 5. Graha Maitri (5 Points) - Mental Compatibility
        val lord1 = rashi1.lordSanskrit
        val lord2 = rashi2.lordSanskrit
        val grahaPoints = when {
            lord1 == lord2 -> 5.0
            // Friendly lord pairs
            (lord1 == "Surya" && lord2 in listOf("Chandra", "Mangala", "Guru")) ||
            (lord1 == "Chandra" && lord2 in listOf("Surya", "Budha")) ||
            (lord1 == "Mangala" && lord2 in listOf("Surya", "Chandra", "Guru")) ||
            (lord1 == "Budha" && lord2 in listOf("Surya", "Shukra")) ||
            (lord1 == "Guru" && lord2 in listOf("Surya", "Chandra", "Mangala")) ||
            (lord1 == "Shukra" && lord2 in listOf("Budha", "Shani")) ||
            (lord1 == "Shani" && lord2 in listOf("Budha", "Shukra")) -> 4.0
            // Incompatible lords (e.g. Surya-Shani, Mars-Mercury)
            (lord1 == "Surya" && lord2 in listOf("Shani", "Shukra")) ||
            (lord1 == "Shani" && lord2 in listOf("Surya", "Chandra", "Mangala")) -> 0.5
            else -> 3.0
        }
        val grahaKoota = KootaScore(
            name = "Graha Maitri",
            devanagari = "ग्रह मैत्री",
            pointsObtained = grahaPoints,
            maxPoints = 5.0,
            attributeCompared = "Psychological & Intellectual Rapport",
            status = if (grahaPoints >= 4.0) "Soul Friends" else if (grahaPoints >= 3.0) "Cordial" else "Diverse Mindsets",
            explanation = if (grahaPoints >= 4.0)
                "Exceptional friendship of Moon lords ($lord1 & $lord2). Thought patterns harmonize effortlessly."
            else
                "Different communicative instincts. Celebrate distinct worldviews rather than debating them."
        )

        // 6. Gana (6 Points) - Temperament Compatibility (Deva, Manushya, Rakshasa)
        val gana1 = nak1.gana
        val gana2 = nak2.gana
        val ganaPoints = when {
            gana1 == gana2 -> 6.0
            (gana1 == Gana.DEVA && gana2 == Gana.MANUSHYA) || (gana1 == Gana.MANUSHYA && gana2 == Gana.DEVA) -> 5.0
            (gana1 == Gana.DEVA && gana2 == Gana.RAKSHASA) || (gana1 == Gana.RAKSHASA && gana2 == Gana.DEVA) -> 1.0
            else -> 0.0 // Manushya & Rakshasa
        }
        val ganaKoota = KootaScore(
            name = "Gana Koota",
            devanagari = "गण कूट",
            pointsObtained = ganaPoints,
            maxPoints = 6.0,
            attributeCompared = "Temperament & Behavioral Style",
            status = if (ganaPoints >= 5.0) "Balanced & Compatible" else "Temperamental Variance",
            explanation = if (ganaPoints >= 5.0)
                "Congenial life approaches (${gana1.devanagari} and ${gana2.devanagari}). Shared expectations and values."
            else
                "Dynamic contrast between behavioral styles. Clear communication avoids feeling misunderstood."
        )

        // 7. Bhakoot (7 Points) - Family Welfare & Emotional Depth
        val rashiDistance = ((rashi2.index - rashi1.index + 12) % 12) + 1
        val isAuspiciousBhakoot = rashiDistance in listOf(1, 3, 4, 7, 10, 11)
        val bhakootPoints = if (isAuspiciousBhakoot || lord1 == lord2) 7.0 else 0.0
        val hasBhakootDosha = bhakootPoints == 0.0
        val bhakootKoota = KootaScore(
            name = "Bhakoot Koota",
            devanagari = "भकूट कूट",
            pointsObtained = bhakootPoints,
            maxPoints = 7.0,
            attributeCompared = "Emotional Bonding & Family Prosperity",
            status = if (!hasBhakootDosha) "Auspicious Prosperity" else "Bhakoot Dosha Present",
            explanation = if (!hasBhakootDosha)
                "Fosters financial growth, joyful progeny, and mutual emotional steadfastness."
            else
                "Bhakoot Dosha (${rashi1.sanskritName} - ${rashi2.sanskritName}). Shared financial planning and patience dissolve tension.",
            remedyIfLow = if (hasBhakootDosha) "Chant Shiva Panchakshara Stotra and donate yellow grains or sweets on Thursdays." else null
        )

        // 8. Nadi (8 Points) - Health & Genetic Constitution
        val nadi1 = nak1.nadi
        val nadi2 = nak2.nadi
        val hasNadiDosha = (nadi1 == nadi2) && (nak1 != nak2 || person1.nakshatraPada == person2.nakshatraPada)
        val nadiPoints = if (!hasNadiDosha) 8.0 else 0.0
        val nadiKoota = KootaScore(
            name = "Nadi Koota",
            devanagari = "नाड़ी कूट",
            pointsObtained = nadiPoints,
            maxPoints = 8.0,
            attributeCompared = "Physiological Health & Genetic Harmony",
            status = if (!hasNadiDosha) "Excellent Health & Lineage" else "Nadi Dosha Active (${nadi1.element})",
            explanation = if (!hasNadiDosha)
                "Different Nadis (${nadi1.devanagari} vs ${nadi2.devanagari}) grant robust vitality, genetic variety, and peace."
            else
                "Same Nadi constitution. Both share identical Ayurvedic elemental polarity.",
            remedyIfLow = if (hasNadiDosha) "Perform Maha Mrityunjaya Japa (108 times daily) and donate gold or copper coin on birth tithi." else null
        )

        val kootas = listOf(
            nadiKoota, bhakootKoota, ganaKoota, grahaKoota,
            yoniKoota, taraKoota, vashyaKoota, varnaKoota
        )
        val total = kootas.sumOf { it.pointsObtained }

        val grade = when {
            total >= 28.0 -> MatchingGrade.EXCELLENT
            total >= 18.0 -> MatchingGrade.GOOD
            total >= 12.0 -> MatchingGrade.AVERAGE
            else -> MatchingGrade.CHALLENGING
        }

        // Manglik Match Analysis
        val m1 = person1.manglikStatus.isManglik && !person1.manglikStatus.isCancelled
        val m2 = person2.manglikStatus.isManglik && !person2.manglikStatus.isCancelled
        val manglikVerdict = when {
            !m1 && !m2 -> "Both are Non-Manglik: Perfectly balanced Manglik compatibility."
            m1 && m2 -> "Both are Manglik: Dosha is mutual and harmoniously neutralized."
            else -> "Single Manglik Alliance: Mars energy disparity. Simple remedies (Hanuman Chalisa, Coral Pooja) create balance."
        }

        val strengths = mutableListOf<String>()
        val frictions = mutableListOf<String>()
        val remedies = mutableListOf<String>()

        if (nadiPoints > 0) strengths.add("Strong physiological vitality and healthy generational synergy (Nadi 8/8).")
        if (bhakootPoints > 0) strengths.add("Promising financial accumulation and domestic peace (Bhakoot 7/7).")
        if (grahaPoints >= 4.0) strengths.add("Warm intellectual kinship and effortless telepathic bond.")
        if (ganaPoints >= 5.0) strengths.add("Harmonious shared lifestyle rhythm and social temperaments.")

        if (hasNadiDosha) {
            frictions.add("Nadi Dosha: Identical Ayurvedic energy (${nadi1.devanagari}). Health mindfulness required.")
            remedies.add("Recite Maha Mrityunjaya Mantra together every Monday.")
        }
        if (hasBhakootDosha) {
            frictions.add("Bhakoot Dosha: Requires collaborative budget handling and transparent communication.")
            remedies.add("Offer Arghya to Surya Dev together at sunrise and feed green grass to cows.")
        }
        if (m1 != m2) {
            remedies.add("Light a mustard oil lamp under a Peepal tree on Saturdays for Saturn & Mars harmony.")
        }
        if (remedies.isEmpty()) {
            remedies.add("Keep a silver coin or pearl in the domestic altar to enhance Chandra grace.")
        }

        return CompatibilityReport(
            person1Name = person1.profileName,
            person1MoonRashi = rashi1,
            person1Nakshatra = nak1,
            person1IsManglik = m1,
            person2Name = person2.profileName,
            person2MoonRashi = rashi2,
            person2Nakshatra = nak2,
            person2IsManglik = m2,
            totalScore = total,
            grade = grade,
            kootas = kootas,
            manglikCompatibility = manglikVerdict,
            hasNadiDosha = hasNadiDosha,
            hasBhakootDosha = hasBhakootDosha,
            relationshipStrengths = strengths,
            potentialFrictionAreas = frictions,
            suggestedRemedies = remedies
        )
    }

    // --- Personalized Gochar / Transits Engine ---

    fun getPersonalizedTransits(chart: KundaliChartData): TransitDashboard {
        val moonRashi = chart.chandraRashi
        val lagnaRashi = chart.lagnaRashi

        // Current real-world astrological transits:
        // Saturn (Shani) is in Meena (Pisces)
        val shaniRashi = Rashi.MEENA
        // Jupiter (Guru) is in Mithuna (Gemini)
        val guruRashi = Rashi.MITHUNA
        // Rahu is in Kumbha (Aquarius)
        val rahuRashi = Rashi.KUMBHA
        // Ketu is in Simha (Leo)
        val ketuRashi = Rashi.SIMHA
        // Sun (Surya) current sign
        val currentMonth = LocalDate.now().monthValue
        val suryaCurrentRashi = Rashi.fromIndex(currentMonth)
        // Mars (Mangala) in Karka (Cancer)
        val mangalaCurrentRashi = Rashi.KARKA

        fun houseDiff(from: Rashi, to: Rashi): Int = ((to.index - from.index + 12) % 12) + 1

        val shaniFromMoon = houseDiff(moonRashi, shaniRashi)
        val shaniFromLagna = houseDiff(lagnaRashi, shaniRashi)

        // Sade Sati calculation
        val sadeSatiPhase = when (shaniFromMoon) {
            12 -> SadeSatiPhase.RISING
            1 -> SadeSatiPhase.PEAK
            2 -> SadeSatiPhase.SETTING
            4, 8 -> SadeSatiPhase.KANTAKA_SHANI
            else -> SadeSatiPhase.NONE
        }

        val sadeSatiAdvice = when (sadeSatiPhase) {
            SadeSatiPhase.RISING -> "Saturn transits the 12th house from ${moonRashi.englishName}. Financial budgeting, foreign ventures, and daily spiritual meditation yield excellent rewards."
            SadeSatiPhase.PEAK -> "Saturn is directly conjoining natal Moon in ${moonRashi.englishName}. Deep character forging. Practice patience, avoid hasty commitments, and honor commitments with integrity."
            SadeSatiPhase.SETTING -> "Saturn is in the 2nd house from your Moon. The most intense phase is clearing. Focus on strengthening family ties and steady savings accumulation."
            SadeSatiPhase.KANTAKA_SHANI -> "Saturn in House $shaniFromMoon from Moon creates Dhaiya. Maintain disciplined work ethics and take adequate physical rest."
            SadeSatiPhase.NONE -> "You are currently outside the sphere of Sade Sati! Saturn in House $shaniFromMoon acts as a builder of enduring karma."
        }

        // Generate transit readings for major planets
        val transits = mutableListOf<PlanetaryTransit>()

        // 1. Shani Gochar
        transits.add(
            PlanetaryTransit(
                planet = Planet.SHANI,
                currentRashi = shaniRashi,
                isRetrograde = false,
                transitFromMoon = shaniFromMoon,
                transitFromLagna = shaniFromLagna,
                impact = if (shaniFromMoon in listOf(3, 6, 11)) TransitImpact.VERY_FAVORABLE else TransitImpact.REQUIRES_CAUTION,
                title = "Saturn in Pisces (${shaniFromMoon}th House from Moon)",
                description = "Shani transit demands deep emotional clarity and dismantling illusions. Affects House $shaniFromMoon: ${Bhava.entries[shaniFromMoon - 1].englishName}.",
                remedies = listOf("Chant Shani Beej Mantra on Saturdays", "Feed black sesame seeds or mustard oil to the needy", "Honor elderly mentors")
            )
        )

        // 2. Guru Gochar
        val guruFromMoon = houseDiff(moonRashi, guruRashi)
        val guruFromLagna = houseDiff(lagnaRashi, guruRashi)
        transits.add(
            PlanetaryTransit(
                planet = Planet.GURU,
                currentRashi = guruRashi,
                isRetrograde = false,
                transitFromMoon = guruFromMoon,
                transitFromLagna = guruFromLagna,
                impact = if (guruFromMoon in listOf(2, 5, 7, 9, 11)) TransitImpact.VERY_FAVORABLE else TransitImpact.FAVORABLE,
                title = "Jupiter in Gemini (${guruFromMoon}th House from Moon)",
                description = "Brihaspati illuminates your intellect and communication sphere. Enhances learning, networking, and creative expression in House $guruFromMoon.",
                remedies = listOf("Wear light yellow clothing on Thursdays", "Recite Guru Stotram", "Donate books or educational tools to students")
            )
        )

        // 3. Rahu & Ketu Gochar
        val rahuFromMoon = houseDiff(moonRashi, rahuRashi)
        transits.add(
            PlanetaryTransit(
                planet = Planet.RAHU,
                currentRashi = rahuRashi,
                isRetrograde = true,
                transitFromMoon = rahuFromMoon,
                transitFromLagna = houseDiff(lagnaRashi, rahuRashi),
                impact = TransitImpact.TRANSFORMATIVE,
                title = "Rahu in Aquarius (${rahuFromMoon}th House from Moon)",
                description = "Rahu creates unconventional breakthroughs, tech interests, and ambitious long-term aspirations in your ${rahuFromMoon}th House.",
                remedies = listOf("Feed birds regularly", "Practice grounding outdoor barefoot walking", "Maintain high ethical transparency in finances")
            )
        )

        // 4. Mars Gochar
        val mangalaFromMoon = houseDiff(moonRashi, mangalaCurrentRashi)
        transits.add(
            PlanetaryTransit(
                planet = Planet.MANGALA,
                currentRashi = mangalaCurrentRashi,
                isRetrograde = false,
                transitFromMoon = mangalaFromMoon,
                transitFromLagna = houseDiff(lagnaRashi, mangalaCurrentRashi),
                impact = if (mangalaFromMoon in listOf(3, 6, 11)) TransitImpact.FAVORABLE else TransitImpact.MIXED_OPPORTUNITY,
                title = "Mars in Cancer (${mangalaFromMoon}th House from Moon)",
                description = "Mars in debilitation sign requires channeling fiery passion into constructive gym workouts, yoga, and calm diplomacy.",
                remedies = listOf("Recite Hanuman Chalisa daily", "Drink water from a copper vessel", "Avoid hot temper in domestic settings")
            )
        )

        val theme = "Cosmic realignment favors strategic discipline. Jupiter in ${guruRashi.englishName} stimulates intellectual expansions, while Saturn in ${shaniRashi.englishName} anchors long-term commitments."

        val upayas = listOf(
            "Recite Gayatri Mantra (24 or 108 times) facing East in the morning.",
            "Water a sacred Tulsi plant daily with pure water.",
            "Wear a Navagraha yantra or chant Om Namah Shivaya for planetary balance."
        )

        return TransitDashboard(
            profileName = chart.profileName,
            moonRashi = moonRashi,
            lagnaRashi = lagnaRashi,
            sadeSatiPhase = sadeSatiPhase,
            sadeSatiAdvice = sadeSatiAdvice,
            activeTransits = transits,
            monthlyCosmicTheme = theme,
            recommendedUpayas = upayas
        )
    }

    // --- Daily Horoscopes & Panchang Engine ---

    fun getDailyHoroscope(rashi: Rashi, date: LocalDate = LocalDate.now()): DailyHoroscope {
        val daySeed = date.toEpochDay() + rashi.index * 17
        val energyScore = 65 + ((daySeed * 13) % 31).toInt() // 65 - 95%

        val loveRatings = listOf(4, 5, 3, 4, 5)
        val careerRatings = listOf(5, 4, 4, 5, 3)
        val wealthRatings = listOf(3, 4, 5, 4, 4)
        val healthRatings = listOf(4, 4, 3, 5, 4)

        val idx = (daySeed % 5).toInt()

        val summaries = listOf(
            "Today's planetary alignment bestows renewed clarity and magnetic confidence. Auspicious currents surround productive conversations and purposeful endeavors.",
            "A serene cosmic transit encourages thoughtful reflection. Trust your intuitive instincts in both professional matters and personal bonds.",
            "Dynamic celestial momentum surrounds you today. Favorable aspects stimulate decisive action and financial opportunities.",
            "Harmonious lunar vibrations grace your emotional center today. Perfect time to reconcile differences and nurture meaningful connections.",
            "An invigorating cosmic wave empowers your focus. Your creative and analytical faculties operate at peak vibrancy."
        )

        val loveForecasts = listOf(
            "Spontaneous affection and deep conversations warm your connection. Singles may experience an intriguing encounter through shared hobbies.",
            "Gentle mutual understanding bridges any past reservations. Express heartfelt gratitude to deepen companionship.",
            "Playful romance and shared laughter spark joyful memories. Plan an intimate evening walk or culinary experience together.",
            "A time for empathetic listening. Creating a safe emotional sanctuary brings you closer than words can convey.",
            "Passionate alignment invites exciting future dreams. Discuss travel or joint aspirations with open enthusiasm."
        )

        val careerForecasts = listOf(
            "Your strategic foresight impresses peers and mentors. A pending proposal or decision turns favorably toward your goals.",
            "Steadfast execution yields tangible progress. Tackle complex tasks in the morning when your mental bandwidth is supreme.",
            "Collaborative energy thrives. Your diplomatic tact resolves a tricky cross-team bottleneck effortlessly.",
            "Leadership qualities shine brightly. Do not hesitate to champion your unique insights during critical discussions.",
            "High productivity and disciplined clarity. Organize your workflow for long-term compounding success."
        )

        val wealthForecasts = listOf(
            "Prudent financial currents. Excellent day for revising budgets, checking investments, and curtailing unnecessary impulse spends.",
            "Auspicious indicators for unexpected gains or reimbursement. Consider putting aside savings for sacred family investments.",
            "Favorable trade and commerce vibrations. An opportunity to expand your income stream may reveal itself through networks.",
            "Financial stability provides peace of mind. Prioritize durable assets and consult knowledgeable advisors before big commitments.",
            "Steady growth. Past efforts begin showing returns. Maintain contentment and avoid speculative gambles."
        )

        val healthForecasts = listOf(
            "Robust vitality and high stamina. Channel this positive prana into brisk morning exercise and mindful breathing.",
            "Good mental composure. Stay well hydrated with herbal teas and prioritize soothing restful sleep tonight.",
            "Vigorous life force. Incorporate stretching or gentle yoga to release neck and shoulder tension from screen time.",
            "Peaceful digestion and balanced energy. Favor warm, freshly prepared sattvic meals with wholesome grains and spices.",
            "Renewed physical enthusiasm. Spend some time in natural sunlight to recharge your solar plexus energy."
        )

        val luckyNumbers = rashi.luckyNumbers
        val luckyNum = luckyNumbers[(daySeed % luckyNumbers.size).toInt()]
        val luckyTime = listOf("09:15 AM - 10:45 AM", "11:30 AM - 01:00 PM", "02:45 PM - 04:15 PM", "05:00 PM - 06:30 PM")[(daySeed % 4).toInt()]

        val mantras = listOf(
            "ॐ नमो भगवते वासुदेवाय (Om Namo Bhagavate Vasudevaya)",
            "ॐ नमः शिवाय (Om Namah Shivaya)",
            "ॐ श्रीं महालक्ष्म्यै नमः (Om Shreem Mahalakshmyai Namah)",
            "ॐ गं गणपतये नमः (Om Gam Ganapataye Namaha)",
            "ॐ सूर्याय नमः (Om Suryaya Namaha)"
        )

        val affirmations = listOf(
            "I walk with divine grace, unshakable peace, and luminous purpose.",
            "Cosmic abundance flows effortlessly into my thoughts, actions, and relationships.",
            "I honor the rhythm of the cosmos and trust the perfect unfolding of my destiny.",
            "My mind is calm, my heart is courageous, and my actions bear auspicious fruit."
        )

        return DailyHoroscope(
            rashi = rashi,
            dateString = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")),
            cosmicEnergyScore = energyScore,
            summary = summaries[idx],
            love = CategoryForecast("Love & Harmony", "favorite", loveRatings[idx], loveForecasts[idx]),
            career = CategoryForecast("Career & Ambition", "work", careerRatings[idx], careerForecasts[idx]),
            wealth = CategoryForecast("Wealth & Fortune", "payments", wealthRatings[idx], wealthForecasts[idx]),
            health = CategoryForecast("Health & Vitality", "spa", healthRatings[idx], healthForecasts[idx]),
            luckyNumber = luckyNum,
            luckyColor = rashi.luckyColor,
            luckyColorHex = 0xFFFFC107,
            luckyTime = luckyTime,
            dailyMantra = mantras[(daySeed % mantras.size).toInt()],
            affirmation = affirmations[(daySeed % affirmations.size).toInt()]
        )
    }

    fun getTodayPanchang(date: LocalDate = LocalDate.now()): PanchangData {
        val tithis = listOf(
            "Shukla Pratipada", "Shukla Dwitiya", "Shukla Tritiya", "Shukla Chaturthi",
            "Shukla Panchami", "Shukla Shashthi", "Shukla Saptami", "Shukla Ashtami",
            "Shukla Navami", "Shukla Dashami", "Shukla Ekadashi", "Shukla Dwadashi",
            "Shukla Trayodashi", "Shukla Chaturdashi", "Purnima (Full Moon)",
            "Krishna Pratipada", "Krishna Dwitiya", "Krishna Tritiya", "Krishna Chaturthi",
            "Krishna Panchami", "Krishna Shashthi", "Krishna Saptami", "Krishna Ashtami",
            "Krishna Navami", "Krishna Dashami", "Krishna Ekadashi", "Krishna Dwadashi",
            "Krishna Trayodashi", "Krishna Chaturdashi", "Amavasya (New Moon)"
        )

        val vaars = mapOf(
            1 to "Somavaar (Monday / Chandra Dev)",
            2 to "Mangalvaar (Tuesday / Mangala Dev)",
            3 to "Budhavaar (Wednesday / Budha Dev)",
            4 to "Guruvaar (Thursday / Brihaspati Dev)",
            5 to "Shukravaar (Friday / Shukra Dev)",
            6 to "Shanivaar (Saturday / Shani Dev)",
            7 to "Ravivaar (Sunday / Surya Dev)"
        )

        val yogas = listOf("Vishkambha", "Priti", "Ayushman", "Saubhagya", "Shobhana", "Sukarma", "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra", "Siddhi", "Vyatipata", "Variyan", "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma", "Indra", "Vaidhriti")
        val karanas = listOf("Bava", "Balava", "Kaulava", "Taitila", "Gara", "Vanija", "Vishti (Bhadra)", "Shakuni", "Chatushpada", "Naga", "Kintughna")

        val dayOfYear = date.dayOfYear
        val tithi = tithis[dayOfYear % 30]
        val vaar = vaars[date.dayOfWeek.value] ?: "Somavaar"
        val nakshatra = Nakshatra.fromIndex((dayOfYear % 27) + 1).sanskritName
        val yoga = yogas[dayOfYear % yogas.size]
        val karana = karanas[dayOfYear % karanas.size]

        // Approximate Rahu Kaal by weekday
        val rahuKaalMap = mapOf(
            1 to "07:30 AM - 09:00 AM",
            2 to "03:00 PM - 04:30 PM",
            3 to "12:00 PM - 01:30 PM",
            4 to "01:30 PM - 03:00 PM",
            5 to "10:30 AM - 12:00 PM",
            6 to "09:00 AM - 10:30 AM",
            7 to "04:30 PM - 06:00 PM"
        )

        return PanchangData(
            dateString = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
            tithi = tithi,
            vaar = vaar,
            nakshatra = "$nakshatra Nakshatra",
            yoga = "$yoga Yoga",
            karana = "$karana Karana",
            rahuKaal = rahuKaalMap[date.dayOfWeek.value] ?: "01:30 PM - 03:00 PM",
            abhijitMuhurat = "11:46 AM - 12:38 PM",
            brahmaMuhurat = "04:22 AM - 05:10 AM",
            sunrise = "06:14 AM",
            sunset = "06:28 PM",
            moonPhase = if ((dayOfYear % 30) < 15) "Waxing Moon (Shukla Paksha)" else "Waning Moon (Krishna Paksha)"
        )
    }
}
