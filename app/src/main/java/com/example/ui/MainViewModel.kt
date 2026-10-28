package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AstroRepository
import com.example.data.UserProfileEntity
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppTab(val title: String, val sanskrit: String) {
    DAILY_HOROSCOPE("Horoscope", "राशिफल"),
    KUNDALI("Kundali", "कुण्डली"),
    TRANSITS("Transits", "गोचर"),
    COMPATIBILITY("Milan", "मिलान")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AstroRepository.getInstance(application)

    val activeProfile: StateFlow<UserProfileEntity?> = repository.activeProfile

    val allProfiles: StateFlow<List<UserProfileEntity>> = repository.getAllProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(AppTab.DAILY_HOROSCOPE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _showProfileManager = MutableStateFlow(false)
    val showProfileManager: StateFlow<Boolean> = _showProfileManager.asStateFlow()

    private val _showAddProfileDialog = MutableStateFlow(false)
    val showAddProfileDialog: StateFlow<Boolean> = _showAddProfileDialog.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun openProfileManager() {
        _showProfileManager.value = true
    }

    fun closeProfileManager() {
        _showProfileManager.value = false
    }

    fun openAddProfileDialog() {
        _showAddProfileDialog.value = true
    }

    fun closeAddProfileDialog() {
        _showAddProfileDialog.value = false
    }

    fun switchActiveProfile(profileId: Long) {
        viewModelScope.launch {
            repository.setActiveProfile(profileId)
            _showProfileManager.value = false
        }
    }

    fun deleteProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    fun createProfile(
        name: String,
        birthDate: String,
        birthTime: String,
        birthPlace: String,
        latitude: Double,
        longitude: Double,
        tzOffset: Double,
        gender: String,
        setAsPrimary: Boolean
    ) {
        viewModelScope.launch {
            repository.saveProfile(
                name = name,
                birthDate = birthDate,
                birthTime = birthTime,
                birthPlace = birthPlace,
                latitude = latitude,
                longitude = longitude,
                tzOffset = tzOffset,
                gender = gender,
                setAsPrimary = setAsPrimary
            )
            _showAddProfileDialog.value = false
            _showProfileManager.value = false
        }
    }

    fun getKundaliChart(profile: UserProfileEntity): KundaliChartData {
        return repository.getKundaliChart(profile)
    }

    fun getTransits(profile: UserProfileEntity): TransitDashboard {
        return repository.getTransits(profile)
    }

    fun calculateCompatibility(p1: UserProfileEntity, p2: UserProfileEntity): CompatibilityReport {
        return repository.getCompatibility(p1, p2)
    }

    fun getDailyHoroscope(rashi: Rashi, date: LocalDate = LocalDate.now()): DailyHoroscope {
        return repository.getDailyHoroscope(rashi, date)
    }

    fun getTodayPanchang(date: LocalDate = LocalDate.now()): PanchangData {
        return repository.getTodayPanchang(date)
    }
}
