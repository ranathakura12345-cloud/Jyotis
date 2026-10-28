package com.example

import com.example.data.JyotishEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBirthChartCalculation() {
        val chart = JyotishEngine.calculateBirthChart(
            name = "Test Native",
            birthDateStr = "1995-10-24",
            birthTimeStr = "08:30",
            birthPlace = "New Delhi, India",
            latitude = 28.6139,
            longitude = 77.2090,
            tzOffset = 5.5
        )

        assertNotNull(chart.lagnaRashi)
        assertNotNull(chart.chandraRashi)
        assertNotNull(chart.nakshatra)
        assertTrue(chart.nakshatraPada in 1..4)
        assertEquals(12, chart.houses.size)
        assertEquals(9, chart.planetPlacements.size)
        assertNotNull(chart.currentDasha)
        assertNotNull(chart.manglikStatus)
    }

    @Test
    fun testAshtakootaCompatibilityMatching() {
        val p1 = JyotishEngine.calculateBirthChart(
            name = "Aarav",
            birthDateStr = "1994-06-15",
            birthTimeStr = "10:00",
            birthPlace = "New Delhi",
            latitude = 28.6139,
            longitude = 77.2090,
            tzOffset = 5.5
        )

        val p2 = JyotishEngine.calculateBirthChart(
            name = "Ananya",
            birthDateStr = "1996-03-20",
            birthTimeStr = "18:45",
            birthPlace = "Mumbai",
            latitude = 19.0760,
            longitude = 72.8777,
            tzOffset = 5.5
        )

        val report = JyotishEngine.calculateCompatibility(p1, p2)

        assertTrue(report.totalScore in 0.0..36.0)
        assertEquals(8, report.kootas.size)
        assertNotNull(report.grade)
        assertTrue(report.relationshipStrengths.isNotEmpty())
        assertTrue(report.suggestedRemedies.isNotEmpty())
    }

    @Test
    fun testPersonalizedTransits() {
        val chart = JyotishEngine.calculateBirthChart(
            name = "Dev",
            birthDateStr = "1992-01-01",
            birthTimeStr = "12:00",
            birthPlace = "Varanasi",
            latitude = 25.3176,
            longitude = 82.9739,
            tzOffset = 5.5
        )

        val dashboard = JyotishEngine.getPersonalizedTransits(chart)

        assertNotNull(dashboard.sadeSatiPhase)
        assertTrue(dashboard.activeTransits.isNotEmpty())
        assertTrue(dashboard.recommendedUpayas.isNotEmpty())
        assertTrue(dashboard.activeTransits.any { it.planet == Planet.SHANI })
        assertTrue(dashboard.activeTransits.any { it.planet == Planet.GURU })
    }

    @Test
    fun testDailyHoroscopeAllRashis() {
        Rashi.entries.forEach { rashi ->
            val horoscope = JyotishEngine.getDailyHoroscope(rashi)
            assertTrue(horoscope.cosmicEnergyScore in 50..100)
            assertTrue(horoscope.love.rating in 1..5)
            assertTrue(horoscope.career.rating in 1..5)
            assertTrue(horoscope.wealth.rating in 1..5)
            assertTrue(horoscope.health.rating in 1..5)
            assertNotNull(horoscope.dailyMantra)
            assertNotNull(horoscope.affirmation)
        }
    }
}
