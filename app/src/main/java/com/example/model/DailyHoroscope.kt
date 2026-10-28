package com.example.model

data class CategoryForecast(
    val title: String,
    val iconName: String,
    val rating: Int, // 1-5
    val forecast: String
)

data class PanchangData(
    val dateString: String,
    val tithi: String,
    val vaar: String, // Weekday with Sanskrit name & ruling planet
    val nakshatra: String,
    val yoga: String,
    val karana: String,
    val rahuKaal: String,
    val abhijitMuhurat: String,
    val brahmaMuhurat: String,
    val sunrise: String,
    val sunset: String,
    val moonPhase: String
)

data class DailyHoroscope(
    val rashi: Rashi,
    val dateString: String,
    val cosmicEnergyScore: Int, // 1 - 100
    val summary: String,
    val love: CategoryForecast,
    val career: CategoryForecast,
    val wealth: CategoryForecast,
    val health: CategoryForecast,
    val luckyNumber: Int,
    val luckyColor: String,
    val luckyColorHex: Long,
    val luckyTime: String,
    val dailyMantra: String,
    val affirmation: String
)
