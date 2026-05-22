package com.example.devicemedialist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.devicemedialist.LocalRepository
import com.example.devicemedialist.data.BackupManager
import com.example.devicemedialist.data.BackupSchedule
import com.example.devicemedialist.data.Platform_setting
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineOnTertiary
import com.example.devicemedialist.theme.CineSurfaceContainerHigh
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary

@Composable
fun SettingsScreen(paddingValues: PaddingValues, backupManager: BackupManager) {
    val repository = LocalRepository.current
    val storage by repository.storageSummary.collectAsState()
    val allPlatforms by repository.allPlatforms.collectAsState()
    val scope = rememberCoroutineScope()
    var showAddPlatformDialog by remember { mutableStateOf(false) }
    var platformToDelete by remember { mutableStateOf<Platform_setting?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { SectionLabel("STORAGE & SYNC") }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            SettingsCard {
                val hasStorage = storage.totalGb > 0f
                StorageRow(
                    usedGb = storage.usedGb,
                    totalGb = storage.totalGb,
                    progress = if (hasStorage) (storage.usedGb / storage.totalGb).coerceIn(0f, 1f) else 0f,
                )
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { SectionLabel("LOCAL BACKUP") }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { BackupCard(backupManager = backupManager) }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { SectionLabel("SOURCE PLATFORMS") }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            SettingsCard {
                if (allPlatforms.isEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "No platforms yet. Tap + to add one.",
                            style = MaterialTheme.typography.labelSmall,
                            color = CineOnSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    allPlatforms.forEachIndexed { index, platform ->
                        PlatformRow(
                            platform = platform,
                            onToggle = { enabled ->
                                scope.launch { repository.setPlatformEnabled(platform.platform, enabled) }
                            },
                            onDelete = { platformToDelete = platform },
                        )
                        if (index < allPlatforms.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = CineGlassBorder,
                            )
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = CineGlassBorder,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAddPlatformDialog = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CineSurfaceContainerHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = CineTertiary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Add Platform",
                        style = MaterialTheme.typography.labelLarge,
                        color = CineTertiary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { SectionLabel("SYSTEM") }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            SettingsCard {
                SettingsNavRow(
                    icon = Icons.Rounded.Info,
                    title = "About CineTrack",
                    subtitle = "Version 4.2.1 (Build 890)",
                )
            }
        }
    }

    if (showAddPlatformDialog) {
        AddPlatformDialog(
            onDismiss = { showAddPlatformDialog = false },
            onConfirm = { name, colorHex ->
                scope.launch { repository.insertPlatform(name, colorHex) }
                showAddPlatformDialog = false
            },
        )
    }

    platformToDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { platformToDelete = null },
            containerColor = CineSurfaceContainerLow,
            title = {
                Text(
                    text = "Delete Platform?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Remove \"${p.display_name}\" from the platform list?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineOnSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { repository.deletePlatform(p.platform) }
                    platformToDelete = null
                }) {
                    Text(text = "Delete", color = CineTertiary)
                }
            },
            dismissButton = {
                TextButton(onClick = { platformToDelete = null }) {
                    Text(text = "Cancel", color = CineOnSurfaceVariant)
                }
            },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = CineOnSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CineSurfaceContainerLow)
            .border(1.dp, CineGlassBorder, MaterialTheme.shapes.large),
    ) {
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconChip(icon)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = CineTertiary,
                checkedThumbColor = CineOnTertiary,
                uncheckedTrackColor = CineSurfaceContainerHigh,
                uncheckedThumbColor = CineOnSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIconChip(icon)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = null,
            tint = CineOnSurfaceVariant,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun StorageRow(usedGb: Float, totalGb: Float, progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SettingsIconChip(Icons.Rounded.Storage)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Local Storage",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${(progress * 100).toInt()}% Used",
                style = MaterialTheme.typography.labelLarge,
                color = CineTertiary,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = CineTertiary,
            trackColor = CineSurfaceContainerHigh,
            strokeCap = StrokeCap.Round,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${usedGb} GB",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${totalGb.toInt()} GB Total",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
        }
    }
}

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

@Composable
private fun PlatformRow(
    platform: Platform_setting,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(parseHexColor(platform.color_hex)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = platform.display_name.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = platform.display_name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (platform.enabled != 0L) "Shown in Add Entry" else "Hidden from Add Entry",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = "Delete",
                tint = CineOnSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
        Switch(
            checked = platform.enabled != 0L,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = CineTertiary,
                checkedThumbColor = CineOnTertiary,
                uncheckedTrackColor = CineSurfaceContainerHigh,
                uncheckedThumbColor = CineOnSurfaceVariant,
            ),
        )
    }
}

@Composable
private fun AddPlatformDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#E50914") }
    val presetColors = listOf(
        "#E50914", "#00A8E1", "#1B4FA8", "#6B2FBE",
        "#1DB954", "#FF9900", "#00CEC9", "#4A4A5A",
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CineSurfaceContainerLow,
        title = {
            Text(
                text = "Add Platform",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Platform Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CineTertiary,
                        unfocusedBorderColor = CineGlassBorder,
                        focusedLabelColor = CineTertiary,
                        unfocusedLabelColor = CineOnSurfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = CineTertiary,
                    ),
                )
                Text(
                    text = "PICK A COLOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineOnSurfaceVariant,
                )
                presetColors.chunked(4).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .border(
                                        width = if (selectedColor == hex) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape,
                                    )
                                    .clickable { selectedColor = hex },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), selectedColor) },
                enabled = name.isNotBlank(),
            ) {
                Text(text = "Add", color = if (name.isNotBlank()) CineTertiary else CineOnSurfaceVariant)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = CineOnSurfaceVariant)
            }
        },
    )
}

@Composable
private fun BackupCard(backupManager: BackupManager) {
    val schedule by backupManager.schedule.collectAsState()
    val lastBackupMs by backupManager.lastBackupMs.collectAsState()
    val scheduleOptions = listOf(
        BackupSchedule.DISABLED to "Off",
        BackupSchedule.DAILY to "Daily",
        BackupSchedule.WEEKLY to "Weekly",
    )
    SettingsCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SettingsIconChip(Icons.Rounded.Save)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto Backup",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = lastBackupMs?.let { backupManager.formatLastBackupTime(it) } ?: "No backup yet",
                        style = MaterialTheme.typography.labelSmall,
                        color = CineOnSurfaceVariant,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                scheduleOptions.forEach { (option, label) ->
                    val isSelected = schedule == option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.small)
                            .background(if (isSelected) CineTertiary.copy(alpha = 0.15f) else CineSurfaceContainerHigh)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CineTertiary else CineGlassBorder,
                                shape = MaterialTheme.shapes.small,
                            )
                            .clickable { backupManager.setSchedule(option) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) CineTertiary else CineOnSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
            TextButton(
                onClick = { backupManager.backupNow() },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    contentColor = CineTertiary,
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CineTertiary.copy(alpha = 0.4f)),
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Backup Now",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = "Saved to: ${backupManager.backupDirectoryPath()}",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsIconChip(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CineSurfaceContainerHigh),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CineTertiary,
            modifier = Modifier.size(20.dp),
        )
    }
}
