package com.example.devicemedialist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.ui.unit.sp
import com.example.devicemedialist.LocalRepository
import com.example.devicemedialist.data.Device
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary

private data class ParsedSeason(val label: String, val episodes: String?)

private fun parseSeasonsDetail(detail: String): List<ParsedSeason> =
    detail.split("|").mapNotNull { raw ->
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return@mapNotNull null
        val colonIdx = trimmed.indexOf(':')
        if (colonIdx >= 0) {
            val seasonPart = trimmed.substring(0, colonIdx)
            ParsedSeason(
                label = if (seasonPart.startsWith("S")) "Season ${seasonPart.substring(1)}" else seasonPart,
                episodes = trimmed.substring(colonIdx + 1),
            )
        } else {
            ParsedSeason(
                label = if (trimmed.startsWith("S")) "Season ${trimmed.substring(1)}" else trimmed,
                episodes = null,
            )
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

private fun detailPosterBrush(platform: String): Brush = when (platform.uppercase()) {
    "NETFLIX" -> Brush.verticalGradient(listOf(Color(0xFF1A0000), Color(0xFF5A0000), Color(0xFF8B0000), Color(0xFF0A0A0A)))
    "PRIME"   -> Brush.verticalGradient(listOf(Color(0xFF001A2A), Color(0xFF003355), Color(0xFF005F8A), Color(0xFF0A0A0A)))
    "DISNEY"  -> Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001540), Color(0xFF1B4FA8), Color(0xFF0A0A0A)))
    else      -> Brush.verticalGradient(listOf(Color(0xFF0A0A14), Color(0xFF1A1A2A), Color(0xFF3A3A4A), Color(0xFF0A0A0A)))
}

private fun detailDeviceIcon(type: String): ImageVector = when (type.uppercase()) {
    "PHONE"    -> Icons.Rounded.PhoneAndroid
    "TABLET"   -> Icons.Rounded.Tablet
    "TV"       -> Icons.Rounded.Tv
    "LAPTOP"   -> Icons.Rounded.Laptop
    "PENDRIVE" -> Icons.Rounded.Usb
    "SSD"      -> Icons.Rounded.Storage
    else       -> Icons.Rounded.PhoneAndroid
}

@Composable
fun EntryDetailScreen(entryId: String, onClose: () -> Unit, onEdit: (String) -> Unit) {
    val repository = LocalRepository.current
    val entries by repository.mediaEntries.collectAsState()
    val allPlatforms by repository.allPlatforms.collectAsState()
    val devices by repository.devicesForEntry(entryId).collectAsState(initial = emptyList())

    val entry = entries.firstOrNull { it.id == entryId }
    val scope = rememberCoroutineScope()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (entry == null) {
        LaunchedEffect(Unit) { onClose() }
        return
    }

    val isSeries = entry.entry_type?.uppercase() == "SERIES"
    val seasons = remember(entry.seasons_detail) {
        entry.seasons_detail?.let { parseSeasonsDetail(it) } ?: emptyList()
    }
    val platform = allPlatforms.firstOrNull { it.platform == entry.platform }
    val platformDisplayName = platform?.display_name ?: entry.platform.lowercase().replaceFirstChar { it.uppercase() }
    val platformBadgeColor = platform?.color_hex?.let { parseHexColor(it) } ?: Color(0xFF2A2A2B)

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
                .padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = CineTertiary,
                )
            }
            Text(
                text = "Entry Details",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = { onEdit(entryId) }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit",
                    tint = CineTertiary,
                    modifier = Modifier.size(20.dp),
                )
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Entry") },
                text = { Text("Remove \"${entry.title}\" from your library? This cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteConfirm = false
                        scope.launch {
                            repository.deleteMediaEntry(entryId)
                            onClose()
                        }
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                },
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            ) {
                if (entry.image_uri != null) {
                    AsyncImage(
                        model = entry.image_uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(detailPosterBrush(entry.platform)),
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(0.65f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                            )
                        ),
                )
                Text(
                    text = platformDisplayName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(platformBadgeColor)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        entry.entry_type?.let { type ->
                            DetailChip(
                                text = type.lowercase().replaceFirstChar { it.uppercase() },
                                color = CineTertiary,
                                bgColor = CineTertiary.copy(alpha = 0.2f),
                            )
                        }
                        entry.release_year?.let { year ->
                            DetailChip(
                                text = year.toString(),
                                color = Color.White.copy(alpha = 0.8f),
                                bgColor = Color.Black.copy(alpha = 0.35f),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                entry.size_gb?.let { size ->
                    DetailSection(label = "FILE SIZE") {
                        MetaRow(label = "Size", value = "%.1f GB".format(size))
                    }
                }

                if (isSeries && seasons.isNotEmpty()) {
                    DetailSection(label = "SEASONS") {
                        seasons.forEachIndexed { index, season ->
                            if (index > 0) {
                                HorizontalDivider(
                                    color = CineGlassBorder,
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                )
                            }
                            MetaRow(
                                label = season.label,
                                value = if (season.episodes != null) "Episodes ${season.episodes}" else "Full season",
                            )
                        }
                    }
                }

                if (devices.isNotEmpty()) {
                    DetailSection(label = "STORED ON") {
                        devices.forEachIndexed { index, device ->
                            if (index > 0) {
                                HorizontalDivider(
                                    color = CineGlassBorder,
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                )
                            }
                            DeviceDetailRow(device)
                        }
                    }
                } else {
                    DetailSection(label = "STORED ON") {
                        MetaRow(label = "Location", value = "Streaming Only")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailChip(text: String, color: Color, bgColor: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}

@Composable
private fun DetailSection(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CineOnSurfaceVariant,
        )
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
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = CineOnSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun DeviceDetailRow(device: Device) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = detailDeviceIcon(device.type),
            contentDescription = null,
            tint = CineTertiary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = device.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            if (device.used_storage_gb != null && device.total_storage_gb != null) {
                Text(
                    text = "${"%.1f".format(device.used_storage_gb)} / ${"%.1f".format(device.total_storage_gb)} GB used",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineOnSurfaceVariant,
                )
            }
        }
        Text(
            text = device.type.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = CineOnSurfaceVariant,
        )
    }
}
