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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.devicemedialist.LocalRepository
import com.example.devicemedialist.data.Device
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineOnTertiary
import com.example.devicemedialist.theme.CineSurfaceContainerHigh
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary
import kotlinx.coroutines.launch

private data class DeviceTypeOption(val type: String, val label: String, val icon: ImageVector)

private val deviceTypeOptions = listOf(
    DeviceTypeOption("PHONE", "Phone", Icons.Rounded.PhoneAndroid),
    DeviceTypeOption("TABLET", "Tablet", Icons.Rounded.Tablet),
    DeviceTypeOption("TV", "TV", Icons.Rounded.Tv),
    DeviceTypeOption("LAPTOP", "Laptop", Icons.Rounded.Laptop),
    DeviceTypeOption("PENDRIVE", "Pendrive", Icons.Rounded.Usb),
    DeviceTypeOption("SSD", "SSD", Icons.Rounded.Storage),
)

private fun deviceIcon(type: String): ImageVector = when (type.uppercase()) {
    "PHONE" -> Icons.Rounded.PhoneAndroid
    "TABLET" -> Icons.Rounded.Tablet
    "TV" -> Icons.Rounded.Tv
    "LAPTOP" -> Icons.Rounded.Laptop
    "PENDRIVE" -> Icons.Rounded.Usb
    "SSD" -> Icons.Rounded.Storage
    else -> Icons.Rounded.PhoneAndroid
}

@Composable
fun DevicesScreen(paddingValues: PaddingValues) {
    val repository = LocalRepository.current
    val devices by repository.devices.collectAsState()
    val entriesPerDevice by repository.entriesPerDevice.collectAsState()
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            DevicesHeader(onAddClick = { showAddDialog = true })
        }
        if (devices.isEmpty()) {
            item { EmptyDevicesHint() }
        } else {
            items(devices) { device ->
                DeviceCard(
                    device = device,
                    titlesDownloaded = entriesPerDevice[device.id] ?: 0L,
                    onDelete = { scope.launch { repository.deleteDevice(device.id) } },
                    onEdit = { name, type, usedGb, totalGb ->
                        scope.launch { repository.updateDevice(device.id, name, type, usedGb, totalGb) }
                    },
                )
            }
        }
    }

    if (showAddDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type, usedGb, totalGb ->
                scope.launch {
                    repository.insertDevice(name, type, usedGb, totalGb)
                }
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun DevicesHeader(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Devices &\nStorage",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Manage your connected devices and offline downloads.",
                style = MaterialTheme.typography.bodyMedium,
                color = CineOnSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onAddClick,
            shape = MaterialTheme.shapes.extraLarge,
            colors = ButtonDefaults.buttonColors(
                containerColor = CineTertiary,
                contentColor = CineOnTertiary,
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Add\nDevice",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun EmptyDevicesHint() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Movie,
                contentDescription = null,
                tint = CineOnSurfaceVariant,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No devices yet",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Text(
                text = "Tap \"Add Device\" to get started",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun DeviceCard(
    device: Device,
    titlesDownloaded: Long,
    onDelete: () -> Unit,
    onEdit: (name: String, type: String, usedGb: Double?, totalGb: Double?) -> Unit,
) {
    val icon = deviceIcon(device.type)
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CineSurfaceContainerLow)
            .border(1.dp, CineGlassBorder, MaterialTheme.shapes.large)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = device.type.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = CineOnSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = CineGlassBorder)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Storage",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (device.used_storage_gb != null && device.total_storage_gb != null)
                    "${"%.1f".format(device.used_storage_gb)} GB / ${"%.1f".format(device.total_storage_gb)} GB"
                else
                    "Streaming Only",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (device.used_storage_gb != null && device.total_storage_gb != null && device.total_storage_gb > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (device.used_storage_gb / device.total_storage_gb).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CineTertiary,
                trackColor = CineSurfaceContainerHigh,
                strokeCap = StrokeCap.Round,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = if (titlesDownloaded > 0) CineTertiary else CineOnSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$titlesDownloaded Titles Downloaded",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { showEditDialog = true },
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit",
                    tint = CineTertiary,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "EDIT",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTertiary,
                    fontWeight = FontWeight.Bold,
                )
            }
            TextButton(
                onClick = { showDeleteConfirm = true },
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                Text(
                    text = "REMOVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE57373),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

    if (showEditDialog) {
        EditDeviceDialog(
            device = device,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, type, usedGb, totalGb ->
                onEdit(name, type, usedGb, totalGb)
                showEditDialog = false
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    text = "Remove Device",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Remove \"${device.name}\"? This won't delete the media entries linked to it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineOnSurfaceVariant,
                )
            },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text(text = "Remove", color = Color(0xFFE57373))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = "Cancel", color = CineTertiary)
                }
            },
            containerColor = CineSurfaceContainerHigh,
        )
    }
}

@Composable
private fun EditDeviceDialog(
    device: Device,
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, usedGb: Double?, totalGb: Double?) -> Unit,
) {
    var name by remember { mutableStateOf(device.name) }
    var selectedType by remember { mutableStateOf(device.type) }
    var hasStorage by remember { mutableStateOf(device.used_storage_gb != null || device.total_storage_gb != null) }
    var usedGbText by remember { mutableStateOf(device.used_storage_gb?.let { "%.1f".format(it) } ?: "") }
    var totalGbText by remember { mutableStateOf(device.total_storage_gb?.let { "%.1f".format(it) } ?: "") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = CineTertiary,
        unfocusedBorderColor = CineGlassBorder,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = CineTertiary,
        focusedContainerColor = CineSurfaceContainerLow,
        unfocusedContainerColor = CineSurfaceContainerLow,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CineSurfaceContainerHigh,
        title = {
            Text(
                text = "Edit Device",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Device name", color = CineOnSurfaceVariant) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "DEVICE TYPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineOnSurfaceVariant,
                )
                deviceTypeOptions.chunked(3).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        rowOptions.forEach { option ->
                            val isSelected = option.type == selectedType
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(if (isSelected) CineTertiary.copy(alpha = 0.15f) else CineSurfaceContainerLow)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) CineTertiary else CineGlassBorder,
                                        shape = MaterialTheme.shapes.small,
                                    )
                                    .clickable { selectedType = option.type }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.label,
                                    tint = if (isSelected) CineTertiary else CineOnSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) CineTertiary else CineOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Has local storage",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = hasStorage,
                        onCheckedChange = { hasStorage = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = CineTertiary,
                            checkedThumbColor = CineOnTertiary,
                            uncheckedTrackColor = CineSurfaceContainerLow,
                            uncheckedThumbColor = CineOnSurfaceVariant,
                        ),
                    )
                }
                if (hasStorage) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = usedGbText,
                            onValueChange = { usedGbText = it },
                            label = { Text("Used (GB)", color = CineOnSurfaceVariant) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.medium,
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = totalGbText,
                            onValueChange = { totalGbText = it },
                            label = { Text("Total (GB)", color = CineOnSurfaceVariant) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.medium,
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val usedGb = if (hasStorage) usedGbText.toDoubleOrNull() ?: 0.0 else null
                        val totalGb = if (hasStorage) totalGbText.toDoubleOrNull() else null
                        onConfirm(name.trim(), selectedType, usedGb, totalGb)
                    }
                },
            ) {
                Text(text = "Save", color = CineTertiary, fontWeight = FontWeight.Bold)
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
private fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, usedGb: Double?, totalGb: Double?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("PHONE") }
    var hasStorage by remember { mutableStateOf(false) }
    var usedGbText by remember { mutableStateOf("") }
    var totalGbText by remember { mutableStateOf("") }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = CineTertiary,
        unfocusedBorderColor = CineGlassBorder,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = CineTertiary,
        focusedContainerColor = CineSurfaceContainerLow,
        unfocusedContainerColor = CineSurfaceContainerLow,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CineSurfaceContainerHigh,
        title = {
            Text(
                text = "Add Device",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Device name", color = CineOnSurfaceVariant) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "DEVICE TYPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineOnSurfaceVariant,
                )
                deviceTypeOptions.chunked(3).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        rowOptions.forEach { option ->
                            val isSelected = option.type == selectedType
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(if (isSelected) CineTertiary.copy(alpha = 0.15f) else CineSurfaceContainerLow)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) CineTertiary else CineGlassBorder,
                                        shape = MaterialTheme.shapes.small,
                                    )
                                    .clickable { selectedType = option.type }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.label,
                                    tint = if (isSelected) CineTertiary else CineOnSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) CineTertiary else CineOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Has local storage",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(
                        checked = hasStorage,
                        onCheckedChange = { hasStorage = it },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = CineTertiary,
                            checkedThumbColor = CineOnTertiary,
                            uncheckedTrackColor = CineSurfaceContainerLow,
                            uncheckedThumbColor = CineOnSurfaceVariant,
                        ),
                    )
                }
                if (hasStorage) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = usedGbText,
                            onValueChange = { usedGbText = it },
                            label = { Text("Used (GB)", color = CineOnSurfaceVariant) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.medium,
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = totalGbText,
                            onValueChange = { totalGbText = it },
                            label = { Text("Total (GB)", color = CineOnSurfaceVariant) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = MaterialTheme.shapes.medium,
                            colors = textFieldColors,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val usedGb = if (hasStorage) usedGbText.toDoubleOrNull() ?: 0.0 else null
                        val totalGb = if (hasStorage) totalGbText.toDoubleOrNull() else null
                        onConfirm(name.trim(), selectedType, usedGb, totalGb)
                    }
                },
            ) {
                Text(text = "Add", color = CineTertiary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = CineOnSurfaceVariant)
            }
        },
    )
}
