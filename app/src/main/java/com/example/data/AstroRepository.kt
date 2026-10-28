package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class AstroRepository(
    private val database: AppDatabase
) {
    private val dao = database.userProfileDao()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _activeProfile = MutableStateFlow<UserProfileEntity?>(null)
    val activeProfile: StateFlow<UserProfileEntity?> = _activeProfile.asStateFlow()

    init {
        repositoryScope.launch {
            checkAndSeedInitialProfiles()
            // Observe primary profile
            dao.getPrimaryProfile().collect { primary ->
                if (primary != null) {
                    _activeProfile.value = primary
                } else {
                    val first = dao.getAllProfiles().firstOrNull()?.firstOrNull()
                    if (first != null) {
                        dao.setPrimaryProfile(first.id)
                        _activeProfile.value = first
                    }
                }
            }
        }
    }

    private suspend fun checkAndSeedInitialProfiles() {
        val count = dao.getProfileCount()
        if (count == 0) {
            // Seed User Profile: "Aarav Sharma" (Born in New Delhi)
            val chart1 = JyotishEngine.calculateBirthChart(
                name = "Aarav Sharma",
                birthDateStr = "1996-08-22",
                birthTimeStr = "09:45",
                birthPlace = "New Delhi, India",
                latitude = 28.6139,
                longitude = 77.2090,
                tzOffset = 5.5
            )
            val profile1 = UserProfileEntity(
                name = "Aarav Sharma",
                birthDate = "1996-08-22",
                birthTime = "09:45",
                birthPlace = "New Delhi, India",
                latitude = 28.6139,
                longitude = 77.2090,
                timezoneOffsetHours = 5.5,
                gender = "Male",
                moonRashiName = chart1.chandraRashi.name,
                lagnaRashiName = chart1.lagnaRashi.name,
                nakshatraName = chart1.nakshatra.name,
                nakshatraPada = chart1.nakshatraPada,
                isPrimary = true
            )
            val id1 = dao.insertProfile(profile1)

            // Seed Partner Profile: "Priya Patel" (Born in Mumbai)
            val chart2 = JyotishEngine.calculateBirthChart(
                name = "Priya Patel",
                birthDateStr = "1997-11-14",
                birthTimeStr = "14:15",
                birthPlace = "Mumbai, India",
                latitude = 19.0760,
                longitude = 72.8777,
                tzOffset = 5.5
            )
            val profile2 = UserProfileEntity(
                name = "Priya Patel",
                birthDate = "1997-11-14",
                birthTime = "14:15",
                birthPlace = "Mumbai, India",
                latitude = 19.0760,
                longitude = 72.8777,
                timezoneOffsetHours = 5.5,
                gender = "Female",
                moonRashiName = chart2.chandraRashi.name,
                lagnaRashiName = chart2.lagnaRashi.name,
                nakshatraName = chart2.nakshatra.name,
                nakshatraPada = chart2.nakshatraPada,
                isPrimary = false
            )
            dao.insertProfile(profile2)

            _activeProfile.value = profile1.copy(id = id1)
        }
    }

    fun getAllProfiles(): Flow<List<UserProfileEntity>> = dao.getAllProfiles()

    suspend fun saveProfile(
        name: String,
        birthDate: String,
        birthTime: String,
        birthPlace: String,
        latitude: Double = 28.6139,
        longitude: Double = 77.2090,
        tzOffset: Double = 5.5,
        gender: String = "Not Specified",
        setAsPrimary: Boolean = false
    ): Long {
        val chart = JyotishEngine.calculateBirthChart(
            name = name,
            birthDateStr = birthDate,
            birthTimeStr = birthTime,
            birthPlace = birthPlace,
            latitude = latitude,
            longitude = longitude,
            tzOffset = tzOffset
        )
        if (setAsPrimary) {
            dao.clearPrimaryFlags()
        }
        val entity = UserProfileEntity(
            name = name,
            birthDate = birthDate,
            birthTime = birthTime,
            birthPlace = birthPlace,
            latitude = latitude,
            longitude = longitude,
            timezoneOffsetHours = tzOffset,
            gender = gender,
            moonRashiName = chart.chandraRashi.name,
            lagnaRashiName = chart.lagnaRashi.name,
            nakshatraName = chart.nakshatra.name,
            nakshatraPada = chart.nakshatraPada,
            isPrimary = setAsPrimary
        )
        val id = dao.insertProfile(entity)
        if (setAsPrimary) {
            _activeProfile.value = entity.copy(id = id)
        }
        return id
    }

    suspend fun setActiveProfile(profileId: Long) {
        dao.clearPrimaryFlags()
        dao.setPrimaryProfile(profileId)
        val profile = dao.getProfileById(profileId)
        if (profile != null) {
            _activeProfile.value = profile
        }
    }

    suspend fun deleteProfile(profile: UserProfileEntity) {
        dao.deleteProfile(profile)
        if (_activeProfile.value?.id == profile.id) {
            val remaining = dao.getAllProfiles().firstOrNull()?.firstOrNull()
            if (remaining != null) {
                setActiveProfile(remaining.id)
            } else {
                _activeProfile.value = null
            }
        }
    }

    fun getKundaliChart(profile: UserProfileEntity): KundaliChartData {
        return JyotishEngine.calculateBirthChart(
            name = profile.name,
            birthDateStr = profile.birthDate,
            birthTimeStr = profile.birthTime,
            birthPlace = profile.birthPlace,
            latitude = profile.latitude,
            longitude = profile.longitude,
            tzOffset = profile.timezoneOffsetHours
        )
    }

    fun getTransits(profile: UserProfileEntity): TransitDashboard {
        val chart = getKundaliChart(profile)
        return JyotishEngine.getPersonalizedTransits(chart)
    }

    fun getCompatibility(p1: UserProfileEntity, p2: UserProfileEntity): CompatibilityReport {
        val chart1 = getKundaliChart(p1)
        val chart2 = getKundaliChart(p2)
        return JyotishEngine.calculateCompatibility(chart1, chart2)
    }

    fun getDailyHoroscope(rashi: Rashi, date: LocalDate = LocalDate.now()): DailyHoroscope {
        return JyotishEngine.getDailyHoroscope(rashi, date)
    }

    fun getTodayPanchang(date: LocalDate = LocalDate.now()): PanchangData {
        return JyotishEngine.getTodayPanchang(date)
    }

    companion object {
        @Volatile
        private var INSTANCE: AstroRepository? = null

        fun getInstance(context: Context): AstroRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val repo = AstroRepository(db)
                INSTANCE = repo
                repo
            }
        }
    }
}
