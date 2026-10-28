package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.AddProfileDialog
import com.example.ui.components.CosmicBackground
import com.example.ui.components.ProfileManagerSheet
import com.example.ui.screens.CompatibilityScreen
import com.example.ui.screens.DailyHoroscopeScreen
import com.example.ui.screens.KundaliScreen
import com.example.ui.screens.TransitsScreen
import com.example.ui.theme.CosmicOutline
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.JyotishTheme
import com.example.ui.theme.StarlightMuted
import com.example.ui.theme.VedicGold
import com.example.ui.theme.VedicGoldLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JyotishTheme {
                JyotishApp()
            }
        }
    }
}

@Composable
fun JyotishApp(viewModel: MainViewModel = viewModel()) {
    val activeProfile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val allProfiles by viewModel.allProfiles.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showProfileManager by viewModel.showProfileManager.collectAsStateWithLifecycle()
    val showAddProfileDialog by viewModel.showAddProfileDialog.collectAsStateWithLifecycle()

    // BackHandler: Return to Daily Horoscope tab if on another screen
    BackHandler(enabled = currentTab != AppTab.DAILY_HOROSCOPE) {
        viewModel.selectTab(AppTab.DAILY_HOROSCOPE)
    }

    CosmicBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = CosmicSurface,
                    contentColor = StarlightMuted,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    // Tab 1: Daily Horoscope
                    NavigationBarItem(
                        selected = currentTab == AppTab.DAILY_HOROSCOPE,
                        onClick = { viewModel.selectTab(AppTab.DAILY_HOROSCOPE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.DAILY_HOROSCOPE) Icons.Filled.WbSunny else Icons.Outlined.WbSunny,
                                contentDescription = "Daily Horoscope"
                            )
                        },
                        label = {
                            Text(
                                text = "Daily",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == AppTab.DAILY_HOROSCOPE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VedicGold,
                            selectedTextColor = VedicGoldLight,
                            indicatorColor = CosmicSurface.copy(alpha = 0.8f),
                            unselectedIconColor = StarlightMuted,
                            unselectedTextColor = StarlightMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_daily")
                    )

                    // Tab 2: Kundali
                    NavigationBarItem(
                        selected = currentTab == AppTab.KUNDALI,
                        onClick = { viewModel.selectTab(AppTab.KUNDALI) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.KUNDALI) Icons.Filled.AutoAwesomeMosaic else Icons.Outlined.AutoAwesomeMosaic,
                                contentDescription = "Kundali"
                            )
                        },
                        label = {
                            Text(
                                text = "Kundali",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == AppTab.KUNDALI) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VedicGold,
                            selectedTextColor = VedicGoldLight,
                            indicatorColor = CosmicSurface.copy(alpha = 0.8f),
                            unselectedIconColor = StarlightMuted,
                            unselectedTextColor = StarlightMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_kundali")
                    )

                    // Tab 3: Transits
                    NavigationBarItem(
                        selected = currentTab == AppTab.TRANSITS,
                        onClick = { viewModel.selectTab(AppTab.TRANSITS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.TRANSITS) Icons.Filled.TravelExplore else Icons.Outlined.TravelExplore,
                                contentDescription = "Transits"
                            )
                        },
                        label = {
                            Text(
                                text = "Transits",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == AppTab.TRANSITS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VedicGold,
                            selectedTextColor = VedicGoldLight,
                            indicatorColor = CosmicSurface.copy(alpha = 0.8f),
                            unselectedIconColor = StarlightMuted,
                            unselectedTextColor = StarlightMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_transits")
                    )

                    // Tab 4: Milan / Compatibility
                    NavigationBarItem(
                        selected = currentTab == AppTab.COMPATIBILITY,
                        onClick = { viewModel.selectTab(AppTab.COMPATIBILITY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.COMPATIBILITY) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Compatibility"
                            )
                        },
                        label = {
                            Text(
                                text = "Milan",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == AppTab.COMPATIBILITY) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = VedicGold,
                            selectedTextColor = VedicGoldLight,
                            indicatorColor = CosmicSurface.copy(alpha = 0.8f),
                            unselectedIconColor = StarlightMuted,
                            unselectedTextColor = StarlightMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_milan")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.DAILY_HOROSCOPE -> DailyHoroscopeScreen(
                        activeProfile = activeProfile,
                        getHoroscopeForRashi = { viewModel.getDailyHoroscope(it) },
                        getPanchang = { viewModel.getTodayPanchang() },
                        onOpenProfileManager = { viewModel.openProfileManager() }
                    )
                    AppTab.KUNDALI -> KundaliScreen(
                        activeProfile = activeProfile,
                        getKundaliChart = { viewModel.getKundaliChart(it) },
                        onOpenProfileManager = { viewModel.openProfileManager() }
                    )
                    AppTab.TRANSITS -> TransitsScreen(
                        activeProfile = activeProfile,
                        getTransits = { viewModel.getTransits(it) },
                        onOpenProfileManager = { viewModel.openProfileManager() }
                    )
                    AppTab.COMPATIBILITY -> CompatibilityScreen(
                        profiles = allProfiles,
                        onCalculateCompatibility = { p1, p2 -> viewModel.calculateCompatibility(p1, p2) },
                        onAddNewProfileClick = { viewModel.openAddProfileDialog() }
                    )
                }
            }
        }
    }

    // Modal Sheet for Profile Switcher & Manager
    if (showProfileManager) {
        ProfileManagerSheet(
            profiles = allProfiles,
            activeProfileId = activeProfile?.id,
            onSelectProfile = { viewModel.switchActiveProfile(it) },
            onDeleteProfile = { viewModel.deleteProfile(it) },
            onAddNewProfileClick = {
                viewModel.closeProfileManager()
                viewModel.openAddProfileDialog()
            },
            onDismiss = { viewModel.closeProfileManager() }
        )
    }

    // Modal Dialog to Add New Birth Profile
    if (showAddProfileDialog) {
        AddProfileDialog(
            onDismiss = { viewModel.closeAddProfileDialog() },
            onSaveProfile = { name, bDate, bTime, place, lat, lon, tz, gender, setPrimary ->
                viewModel.createProfile(name, bDate, bTime, place, lat, lon, tz, gender, setPrimary)
            }
        )
    }
}
