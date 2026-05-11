package com.example.devicemedialist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicemedialist.LocalRepository
import com.example.devicemedialist.data.SelectAllWithFirstDevice
import com.example.devicemedialist.data.StorageSummary
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineOnTertiary
import com.example.devicemedialist.theme.CineSurfaceContainerHigh
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary

private fun platformBadgeColor(platform: String): Color = when (platform.uppercase()) {
    "NETFLIX" -> Color(0xFFE50914)
    "PRIME" -> Color(0xFF00A8E1)
    "DISNEY" -> Color(0xFF1B4FA8)
    else -> Color(0xFF2A2A2B)
}

private fun platformPosterBrush(platform: String): Brush = when (platform.uppercase()) {
    "NETFLIX" -> Brush.verticalGradient(listOf(Color(0xFF1A0000), Color(0xFF5A0000), Color(0xFF8B0000), Color(0xFF0A0A0A)))
    "PRIME" -> Brush.verticalGradient(listOf(Color(0xFF001A2A), Color(0xFF003355), Color(0xFF005F8A), Color(0xFF0A0A0A)))
    "DISNEY" -> Brush.verticalGradient(listOf(Color(0xFF000A1A), Color(0xFF001540), Color(0xFF1B4FA8), Color(0xFF0A0A0A)))
    else -> Brush.verticalGradient(listOf(Color(0xFF0A0A14), Color(0xFF1A1A2A), Color(0xFF3A3A4A), Color(0xFF0A0A0A)))
}

private fun deviceIcon(type: String?): ImageVector? = when (type?.uppercase()) {
    "PHONE" -> Icons.Rounded.PhoneAndroid
    "TABLET" -> Icons.Rounded.Tablet
    "TV" -> Icons.Rounded.Tv
    "LAPTOP" -> Icons.Rounded.Laptop
    "PENDRIVE" -> Icons.Rounded.Usb
    "SSD" -> Icons.Rounded.Storage
    else -> null
}

private fun formatStorage(gb: Float): String = if (gb >= 1000f) {
    val tb = gb / 1000f
    "%.1f TB".format(tb)
} else {
    "%.1f GB".format(gb)
}

@Composable
fun HomeScreen(paddingValues: PaddingValues, onAddEntry: () -> Unit) {
    val repository = LocalRepository.current
    val entries by repository.mediaEntries.collectAsState()
    val storage by repository.storageSummary.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = paddingValues.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            StorageSummaryCard(storage)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AddNewEntryButton(onAddEntry = onAddEntry)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            LibrarySectionHeader()
        }
        if (entries.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyLibraryHint()
            }
        } else {
            items(entries) { entry ->
                MediaCard(entry)
            }
        }
    }
}

@Composable
private fun StorageSummaryCard(storage: StorageSummary) {
    val hasStorage = storage.totalGb > 0f
    val progress = if (hasStorage) (storage.usedGb / storage.totalGb).coerceIn(0f, 1f) else 0f
    val usedLabel = if (hasStorage) formatStorage(storage.usedGb) else "—"
    val totalLabel = if (hasStorage) formatStorage(storage.totalGb) else "No devices"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(CineSurfaceContainerLow)
            .border(1.dp, CineGlassBorder, MaterialTheme.shapes.large)
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Cloud,
                    contentDescription = null,
                    tint = CineOnSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Storage Summary",
                    style = MaterialTheme.typography.labelLarge,
                    color = CineOnSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CineTertiary),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = usedLabel,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (hasStorage) "of $totalLabel Used" else totalLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineOnSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
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
        }
    }
}

@Composable
private fun AddNewEntryButton(onAddEntry: () -> Unit) {
    Button(
        onClick = onAddEntry,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = CineTertiary,
            contentColor = CineOnTertiary,
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.AddCircleOutline,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Add New Entry",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun LibrarySectionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "My Library",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {}) {
            Text(
                text = "Filter",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = null,
                tint = CineOnSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun EmptyLibraryHint() {
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
                text = "No entries yet",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Text(
                text = "Tap \"Add New Entry\" to get started",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun MediaCard(entry: SelectAllWithFirstDevice) {
    val icon: ImageVector? = deviceIcon(entry.first_device_type)
    val badgeColor = platformBadgeColor(entry.platform)
    val posterBrush = platformPosterBrush(entry.platform)
    val subtitle = buildString {
        entry.genre?.let { append(it) }
        entry.release_year?.let {
            if (isNotEmpty()) append(" • ")
            append(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(posterBrush),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.55f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                ),
        )
        if (icon != null) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(
            text = entry.platform.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(badgeColor)
                .padding(horizontal = 5.dp, vertical = 2.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
        ) {
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = CineOnSurfaceVariant,
                )
            }
            Text(
                text = entry.title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
