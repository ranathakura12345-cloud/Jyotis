package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Nakshatra
import com.example.model.Rashi

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val birthDate: String, // YYYY-MM-DD
    val birthTime: String, // HH:mm
    val birthPlace: String,
    val latitude: Double = 28.6139, // Default New Delhi
    val longitude: Double = 77.2090,
    val timezoneOffsetHours: Double = 5.5,
    val gender: String = "Not Specified",
    val moonRashiName: String = "MESHA",
    val lagnaRashiName: String = "MESHA",
    val nakshatraName: String = "ASHWINI",
    val nakshatraPada: Int = 1,
    val isPrimary: Boolean = false
) {
    fun getMoonRashi(): Rashi = Rashi.fromName(moonRashiName)
    fun getLagnaRashi(): Rashi = Rashi.fromName(lagnaRashiName)
    fun getNakshatra(): Nakshatra = Nakshatra.fromName(nakshatraName)
}
