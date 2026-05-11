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
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Save
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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

private enum class Platform(val label: String, val iconText: String, val bgColor: Color) {
    NETFLIX("Netflix", "N", Color(0xFFE50914)),
    PRIME("Prime", "P", Color(0xFF00A8E1)),
    DISNEY("Disney+", "D+", Color(0xFF1B4FA8)),
    LOCAL("Local", "", Color(0xFF4A4A5A)),
}

private val platformList = listOf(Platform.NETFLIX, Platform.PRIME, Platform.DISNEY, Platform.LOCAL)

private data class DeviceOption(val name: String, val storage: String, val icon: ImageVector)

private val deviceOptions = listOf(
    DeviceOption("iPhone 15 Pro", "12 GB Available", Icons.Rounded.PhoneAndroid),
    DeviceOption("MacBook Pro", "240 GB Available", Icons.Rounded.Laptop),
    DeviceOption("Home TV Server", "2.4 TB Available", Icons.Rounded.Dns),
)

private val genres = listOf(
    "Action", "Animation", "Comedy", "Documentary",
    "Drama", "Fantasy", "Horror", "Romance", "Sci-Fi", "Thriller",
)

@Composable
fun AddEntryScreen(onCancel: () -> Unit, onSave: () -> Unit) {
    var selectedType by remember { mutableStateOf(EntryType.MOVIE) }
    var title by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf(Platform.PRIME) }
    var selectedDevices by remember { mutableStateOf(setOf("MacBook Pro")) }

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
            TextButton(onClick = onSave) {
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
                PlatformSelector(selected = selectedPlatform, onPlatformSelected = { selectedPlatform = it })
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FormSectionLabel("DOWNLOADED ON")
                DeviceSelector(
                    selectedDevices = selectedDevices,
                    onSelectionChanged = { selectedDevices = it },
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            SaveEntryButton(onClick = onSave)
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
private fun PlatformSelector(selected: Platform, onPlatformSelected: (Platform) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        platformList.forEach { platform ->
            PlatformButton(
                platform = platform,
                isSelected = selected == platform,
                onClick = { onPlatformSelected(platform) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlatformButton(
    platform: Platform,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
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
                .background(platform.bgColor),
            contentAlignment = Alignment.Center,
        ) {
            if (platform == Platform.LOCAL) {
                Icon(
                    imageVector = Icons.Rounded.FolderOpen,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            } else {
                Text(
                    text = platform.iconText,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(
            text = platform.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) CineTertiary else CineOnSurfaceVariant,
        )
    }
}

@Composable
private fun DeviceSelector(
    selectedDevices: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        deviceOptions.forEach { device ->
            val isSelected = device.name in selectedDevices
            DeviceRow(
                device = device,
                isSelected = isSelected,
                onToggle = {
                    onSelectionChanged(
                        if (isSelected) selectedDevices - device.name
                        else selectedDevices + device.name
                    )
                },
            )
        }
    }
}

@Composable
private fun DeviceRow(device: DeviceOption, isSelected: Boolean, onToggle: () -> Unit) {
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
            imageVector = device.icon,
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
                text = device.storage,
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
