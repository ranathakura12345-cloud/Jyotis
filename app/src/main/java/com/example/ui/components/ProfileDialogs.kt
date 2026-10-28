package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.JyotishEngine
import com.example.data.UserProfileEntity
import com.example.ui.theme.*

data class CityPreset(
    val name: String,
    val lat: Double,
    val lon: Double,
    val tz: Double
)

val CITY_PRESETS = listOf(
    CityPreset("New Delhi, India", 28.6139, 77.2090, 5.5),
    CityPreset("Varanasi, India", 25.3176, 82.9739, 5.5),
    CityPreset("Mumbai, India", 19.0760, 72.8777, 5.5),
    CityPreset("Bengaluru, India", 12.9716, 77.5946, 5.5),
    CityPreset("Jaipur, India", 26.9124, 75.7873, 5.5),
    CityPreset("Kolkata, India", 22.5726, 88.3639, 5.5),
    CityPreset("Chennai, India", 13.0827, 80.2707, 5.5),
    CityPreset("London, UK", 51.5074, -0.1278, 0.0),
    CityPreset("New York, USA", 40.7128, -74.0060, -5.0),
    CityPreset("San Francisco, USA", 37.7749, -122.4194, -8.0),
    CityPreset("Dubai, UAE", 25.2048, 55.2708, 4.0),
    CityPreset("Singapore", 1.3521, 103.8198, 8.0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagerSheet(
    profiles: List<UserProfileEntity>,
    activeProfileId: Long?,
    onSelectProfile: (Long) -> Unit,
    onDeleteProfile: (UserProfileEntity) -> Unit,
    onAddNewProfileClick: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CosmicSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = VedicGold) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saved Kundali Profiles",
                        style = MaterialTheme.typography.titleLarge,
                        color = VedicGoldLight
                    )
                    Text(
                        text = "Select active profile for personalized readings",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted
                    )
                }
                FilledTonalButton(
                    onClick = onAddNewProfileClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = VedicGoldContainer,
                        contentColor = VedicGoldLight
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Profile", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(profiles) { profile ->
                    val isSelected = profile.id == activeProfileId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectProfile(profile.id) },
                        color = if (isSelected) CosmicSurfaceVariant else CosmicBackground,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VedicGold else CosmicOutline
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) VedicGoldContainer else CosmicCardElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (isSelected) VedicGoldLight else StarlightMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = StarlightWhite
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = VedicGold.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Active",
                                                    fontSize = 10.sp,
                                                    color = VedicGoldLight,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${profile.getMoonRashi().sanskritName} Rashi • ${profile.getLagnaRashi().sanskritName} Lagna",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = VedicGoldLight
                                    )
                                    Text(
                                        text = "${profile.birthDate} at ${profile.birthTime} • ${profile.birthPlace}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StarlightMuted,
                                        maxLines = 1
                                    )
                                }
                            }

                            if (profiles.size > 1) {
                                IconButton(
                                    onClick = { onDeleteProfile(profile) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete Profile",
                                        tint = StarlightMuted.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileDialog(
    onDismiss: () -> Unit,
    onSaveProfile: (name: String, birthDate: String, birthTime: String, place: String, lat: Double, lon: Double, tz: Double, gender: String, setAsPrimary: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("1998-05-15") }
    var birthTime by remember { mutableStateOf("10:30") }
    var selectedCity by remember { mutableStateOf(CITY_PRESETS[0]) }
    var gender by remember { mutableStateOf("Not Specified") }
    var setAsPrimary by remember { mutableStateOf(true) }

    // Live preview calculation
    val liveChart = remember(birthDate, birthTime, selectedCity) {
        try {
            JyotishEngine.calculateBirthChart(
                name = if (name.isBlank()) "Guest" else name,
                birthDateStr = birthDate,
                birthTimeStr = birthTime,
                birthPlace = selectedCity.name,
                latitude = selectedCity.lat,
                longitude = selectedCity.lon,
                tzOffset = selectedCity.tz
            )
        } catch (e: Exception) {
            null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CosmicSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, VedicGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Kundali Profile",
                        style = MaterialTheme.typography.titleLarge,
                        color = VedicGoldLight
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StarlightMuted)
                    }
                }

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("e.g. Ananya Sen") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VedicGold,
                        unfocusedBorderColor = CosmicOutline,
                        focusedLabelColor = VedicGoldLight,
                        unfocusedLabelColor = StarlightMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Date & Time row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VedicGold,
                            unfocusedBorderColor = CosmicOutline
                        ),
                        modifier = Modifier.weight(1.2f)
                    )

                    OutlinedTextField(
                        value = birthTime,
                        onValueChange = { birthTime = it },
                        label = { Text("Time (HH:mm)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VedicGold,
                            unfocusedBorderColor = CosmicOutline
                        ),
                        modifier = Modifier.weight(0.8f)
                    )
                }

                // Birth City dropdown selector
                Column {
                    Text(
                        text = "Birth Place (Coordinates & Timezone)",
                        style = MaterialTheme.typography.bodySmall,
                        color = StarlightMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    var cityMenuExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = cityMenuExpanded,
                        onExpandedChange = { cityMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCity.name,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityMenuExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VedicGold,
                                unfocusedBorderColor = CosmicOutline
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = cityMenuExpanded,
                            onDismissRequest = { cityMenuExpanded = false },
                            modifier = Modifier.background(CosmicSurfaceVariant)
                        ) {
                            CITY_PRESETS.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city.name, color = StarlightWhite) },
                                    onClick = {
                                        selectedCity = city
                                        cityMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Gender selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Male", "Female", "Other").forEach { opt ->
                        FilterChip(
                            selected = gender == opt,
                            onClick = { gender = opt },
                            label = { Text(opt) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VedicGoldContainer,
                                selectedLabelColor = VedicGoldLight
                            )
                        )
                    }
                }

                // Live Astrological Preview Card
                if (liveChart != null) {
                    Surface(
                        color = CosmicCardElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Lagna (Asc)", fontSize = 10.sp, color = StarlightMuted)
                                Text(liveChart.lagnaRashi.sanskritName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Moon Sign", fontSize = 10.sp, color = StarlightMuted)
                                Text(liveChart.chandraRashi.sanskritName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VedicGoldLight)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Nakshatra", fontSize = 10.sp, color = StarlightMuted)
                                Text("${liveChart.nakshatra.sanskritName} P${liveChart.nakshatraPada}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
                            }
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = StarlightMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalName = if (name.isBlank()) "Kundali ${System.currentTimeMillis() % 1000}" else name.trim()
                            onSaveProfile(
                                finalName,
                                birthDate.trim(),
                                birthTime.trim(),
                                selectedCity.name,
                                selectedCity.lat,
                                selectedCity.lon,
                                selectedCity.tz,
                                gender,
                                setAsPrimary
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VedicGold,
                            contentColor = Color(0xFF241700)
                        )
                    ) {
                        Text("Calculate & Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
