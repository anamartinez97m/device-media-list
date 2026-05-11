package com.example.devicemedialist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.devicemedialist.LocalRepository
import com.example.devicemedialist.data.Device
import com.example.devicemedialist.data.Platform_setting
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineOnTertiary
import com.example.devicemedialist.theme.CineSurfaceContainerHigh
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary

private enum class EntryType { MOVIE, SERIES }

private fun parseHexColor(hex: String): Color {
    val clean = hex.removePrefix("#")
    return try {
        when (clean.length) {
            6 -> Color(0xFF000000L or clean.toLong(16))
            8 -> Color(clean.toLong(16))
            else -> Color(0xFF2A2A2BL)
        }
    } catch (_: NumberFormatException) { Color(0xFF2A2A2BL) }
}

private fun deviceIcon(type: String): ImageVector = when (type.uppercase()) {
    "PHONE" -> Icons.Rounded.PhoneAndroid
    "TABLET" -> Icons.Rounded.Tablet
    "TV" -> Icons.Rounded.Tv
    "LAPTOP" -> Icons.Rounded.Laptop
    "PENDRIVE" -> Icons.Rounded.Usb
    "SSD" -> Icons.Rounded.Storage
    else -> Icons.Rounded.PhoneAndroid
}

private fun deviceStorageLabel(device: Device): String {
    val used = device.used_storage_gb
    val total = device.total_storage_gb
    return if (used != null && total != null) {
        val available = (total - used).coerceAtLeast(0.0)
        if (available >= 1000.0) "${ "%.1f".format(available / 1000.0)} TB Available"
        else "${ "%.1f".format(available)} GB Available"
    } else "Streaming Only"
}

private val genres = listOf(
    "Action", "Animation", "Comedy", "Documentary",
    "Drama", "Fantasy", "Horror", "Romance", "Sci-Fi", "Thriller",
)

@Composable
fun AddEntryScreen(onCancel: () -> Unit, onSave: () -> Unit) {
    val repository = LocalRepository.current
    val scope = rememberCoroutineScope()
    val devices by repository.devices.collectAsState()

    var selectedType by remember { mutableStateOf(EntryType.MOVIE) }
    var title by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf<Platform_setting?>(null) }
    var selectedDeviceIds by remember { mutableStateOf(emptySet<String>()) }

    val enabledPlatforms by repository.enabledPlatforms.collectAsState()

    LaunchedEffect(enabledPlatforms) {
        if (enabledPlatforms.isNotEmpty() && selectedPlatform !in enabledPlatforms) {
            selectedPlatform = enabledPlatforms.first()
        }
    }

    val doSave: () -> Unit = {
        if (title.isNotBlank()) {
            scope.launch {
                repository.insertMediaEntry(
                    title = title.trim(),
                    platform = selectedPlatform?.platform ?: "",
                    entryType = selectedType.name,
                    releaseYear = releaseYear.toLongOrNull(),
                    genre = selectedGenre.ifEmpty { null },
                    deviceIds = selectedDeviceIds.toList(),
                )
                onSave()
            }
        } else {
            onSave()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CineSurfaceContainerLow.copy(alpha = 0.92f))
                .drawBehind {
                    drawLine(
                        color = CineGlassBorder,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                }
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onCancel) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = CineTertiary,
                )
            }
            Text(
                text = "Add New Entry",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = doSave) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.labelLarge,
                    color = CineTertiary,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TypeToggle(selected = selectedType, onTypeSelected = { selectedType = it })
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FormSectionLabel("TITLE")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Enter movie or series title...",
                            color = CineOnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = formTextFieldColors(),
                    singleLine = true,
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FormSectionLabel("RELEASE YEAR")
                    OutlinedTextField(
                        value = releaseYear,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) releaseYear = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("YYYY", color = CineOnSurfaceVariant) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = MaterialTheme.shapes.medium,
                        colors = formTextFieldColors(),
                        singleLine = true,
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FormSectionLabel("GENRE")
                    GenreDropdown(selected = selectedGenre, onGenreSelected = { selectedGenre = it })
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FormSectionLabel("SOURCE PLATFORM")
                PlatformSelector(
                    platforms = enabledPlatforms,
                    selected = selectedPlatform,
                    onPlatformSelected = { selectedPlatform = it },
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FormSectionLabel("DOWNLOADED ON")
                DeviceSelector(
                    devices = devices,
                    selectedDeviceIds = selectedDeviceIds,
                    onSelectionChanged = { selectedDeviceIds = it },
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            SaveEntryButton(onClick = doSave)
        }
    }
}

@Composable
private fun TypeToggle(selected: EntryType, onTypeSelected: (EntryType) -> Unit) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(CineSurfaceContainerLow)
            .border(1.dp, CineGlassBorder, MaterialTheme.shapes.extraLarge)
            .padding(4.dp),
    ) {
        TypeTab("Movie", selected == EntryType.MOVIE) { onTypeSelected(EntryType.MOVIE) }
        TypeTab("Series", selected == EntryType.SERIES) { onTypeSelected(EntryType.SERIES) }
    }
}

@Composable
private fun TypeTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(if (isSelected) CineSurfaceContainerHigh else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 28.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else CineOnSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun FormSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = CineOnSurfaceVariant,
    )
}

@Composable
private fun GenreDropdown(selected: String, onGenreSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(CineSurfaceContainerLow)
                .border(1.dp, CineGlassBorder, MaterialTheme.shapes.medium)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selected.ifEmpty { "Select Genre" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected.isEmpty()) CineOnSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                tint = CineOnSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CineSurfaceContainerHigh),
        ) {
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = { onGenreSelected(genre); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun PlatformSelector(
    platforms: List<Platform_setting>,
    selected: Platform_setting?,
    onPlatformSelected: (Platform_setting) -> Unit,
) {
    if (platforms.isEmpty()) {
        Text(
            text = "No platforms available. Add one in Settings.",
            style = MaterialTheme.typography.labelSmall,
            color = CineOnSurfaceVariant,
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            platforms.forEach { platform ->
                PlatformButton(
                    platform = platform,
                    isSelected = selected == platform,
                    onClick = { onPlatformSelected(platform) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PlatformButton(
    platform: Platform_setting,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val badgeColor = parseHexColor(platform.color_hex)
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(if (isSelected) CineSurfaceContainerHigh else CineSurfaceContainerLow)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) CineTertiary else CineGlassBorder,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = platform.display_name.take(2).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = platform.display_name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) CineTertiary else CineOnSurfaceVariant,
        )
    }
}

@Composable
private fun DeviceSelector(
    devices: List<Device>,
    selectedDeviceIds: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
) {
    if (devices.isEmpty()) {
        Text(
            text = "No devices added yet. Go to Devices tab to add one.",
            style = MaterialTheme.typography.labelSmall,
            color = CineOnSurfaceVariant,
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            devices.forEach { device ->
                val isSelected = device.id in selectedDeviceIds
                DeviceRow(
                    device = device,
                    isSelected = isSelected,
                    onToggle = {
                        onSelectionChanged(
                            if (isSelected) selectedDeviceIds - device.id
                            else selectedDeviceIds + device.id
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun DeviceRow(device: Device, isSelected: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(if (isSelected) CineTertiary.copy(alpha = 0.08f) else CineSurfaceContainerLow)
            .border(
                1.dp,
                if (isSelected) CineTertiary.copy(alpha = 0.35f) else CineGlassBorder,
                MaterialTheme.shapes.medium,
            )
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = deviceIcon(device.type),
            contentDescription = null,
            tint = if (isSelected) CineTertiary else CineOnSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) CineTertiary else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = deviceStorageLabel(device),
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = CineTertiary,
                uncheckedColor = CineOnSurfaceVariant,
                checkmarkColor = CineOnTertiary,
            ),
        )
    }
}

@Composable
private fun SaveEntryButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CineSurfaceContainerHigh)
            .border(1.dp, CineTertiary.copy(alpha = 0.4f), MaterialTheme.shapes.large)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Save,
            contentDescription = null,
            tint = CineTertiary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Save Entry",
            style = MaterialTheme.typography.labelLarge,
            color = CineTertiary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CineTertiary,
    unfocusedBorderColor = CineGlassBorder,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = CineTertiary,
    focusedContainerColor = CineSurfaceContainerLow,
    unfocusedContainerColor = CineSurfaceContainerLow,
)
