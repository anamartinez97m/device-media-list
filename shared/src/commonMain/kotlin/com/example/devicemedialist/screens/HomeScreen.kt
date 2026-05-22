package com.example.devicemedialist.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Laptop
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import androidx.compose.ui.text.style.TextOverflow
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

private fun formatSeasonsDetail(detail: String): String {
    val parts = detail.split("|")
    return if (parts.size == 1) parts[0].replace(":", " E")
    else "${parts.size} Seasons"
}

private fun formatStorage(gb: Float): String = if (gb >= 1000f) {
    val tb = gb / 1000f
    "%.1f TB".format(tb)
} else {
    "%.1f GB".format(gb)
}

@Composable
fun HomeScreen(paddingValues: PaddingValues, onAddEntry: () -> Unit, onEntryClick: (String) -> Unit, onEditEntry: (String) -> Unit, searchQuery: String = "") {
    val repository = LocalRepository.current
    val entries by repository.mediaEntries.collectAsState()
    val storage by repository.storageSummary.collectAsState()

    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedPlatform by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    val availablePlatforms = remember(entries) {
        entries.map { it.platform.uppercase() }.distinct().sorted()
    }

    val filteredEntries = remember(entries, searchQuery, selectedType, selectedPlatform) {
        entries.filter { entry ->
            val matchesSearch = searchQuery.isBlank() || entry.title.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == null || entry.entry_type?.uppercase() == selectedType
            val matchesPlatform = selectedPlatform == null || entry.platform.uppercase() == selectedPlatform
            matchesSearch && matchesType && matchesPlatform
        }
    }

    val activeFilterCount = listOfNotNull(selectedType, selectedPlatform).size
    val isFiltering = searchQuery.isNotBlank() || activeFilterCount > 0

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
            LibrarySectionHeader(
                activeFilterCount = activeFilterCount,
                showFilters = showFilters,
                onFilterClick = { showFilters = !showFilters },
            )
        }
        if (showFilters) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LibraryFilterPanel(
                    selectedType = selectedType,
                    onTypeSelected = { t -> selectedType = if (selectedType == t) null else t },
                    selectedPlatform = selectedPlatform,
                    onPlatformSelected = { p -> selectedPlatform = if (selectedPlatform == p) null else p },
                    availablePlatforms = availablePlatforms,
                    onClearAll = { selectedType = null; selectedPlatform = null },
                )
            }
        }
        when {
            filteredEntries.isEmpty() && isFiltering -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    NoResultsHint()
                }
            }
            filteredEntries.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyLibraryHint()
                }
            }
            else -> {
                items(filteredEntries) { entry ->
                    MediaCard(entry, onEntryClick, onEditEntry)
                }
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
private fun LibrarySectionHeader(
    activeFilterCount: Int,
    showFilters: Boolean,
    onFilterClick: () -> Unit,
) {
    val filterActive = activeFilterCount > 0
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
        TextButton(onClick = onFilterClick) {
            if (filterActive) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(CineTertiary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = activeFilterCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CineOnTertiary,
                        fontSize = 10.sp,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = if (showFilters) "Hide" else "Filter",
                style = MaterialTheme.typography.labelLarge,
                color = if (filterActive) CineTertiary else CineOnSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = null,
                tint = if (filterActive) CineTertiary else CineOnSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun LibraryFilterPanel(
    selectedType: String?,
    onTypeSelected: (String) -> Unit,
    selectedPlatform: String?,
    onPlatformSelected: (String) -> Unit,
    availablePlatforms: List<String>,
    onClearAll: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(CineSurfaceContainerLow)
            .border(1.dp, CineGlassBorder, MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Type",
                style = MaterialTheme.typography.labelMedium,
                color = CineOnSurfaceVariant,
            )
            FilterPill(label = "Movie", selected = selectedType == "MOVIE") { onTypeSelected("MOVIE") }
            FilterPill(label = "Series", selected = selectedType == "SERIES") { onTypeSelected("SERIES") }
        }
        if (availablePlatforms.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Platform",
                    style = MaterialTheme.typography.labelMedium,
                    color = CineOnSurfaceVariant,
                )
                availablePlatforms.forEach { platform ->
                    val label = platform.take(1) + platform.drop(1).lowercase()
                    FilterPill(label = label, selected = selectedPlatform == platform) { onPlatformSelected(platform) }
                }
            }
        }
        if (selectedType != null || selectedPlatform != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onClearAll) {
                    Text(
                        text = "Clear filters",
                        style = MaterialTheme.typography.labelMedium,
                        color = CineOnSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) CineTertiary else CineSurfaceContainerHigh
    val textColor = if (selected) CineOnTertiary else CineOnSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, if (selected) CineTertiary else CineGlassBorder, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
        )
    }
}

@Composable
private fun NoResultsHint() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = CineOnSurfaceVariant,
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No results found",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Text(
                text = "Try adjusting your search or filters",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant.copy(alpha = 0.6f),
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaCard(entry: SelectAllWithFirstDevice, onEntryClick: (String) -> Unit, onEditEntry: (String) -> Unit) {
    val repository = LocalRepository.current
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val icon: ImageVector? = deviceIcon(entry.first_device_type)
    val badgeColor = platformBadgeColor(entry.platform)
    val posterBrush = platformPosterBrush(entry.platform)
    val subtitle = buildString {
        entry.release_year?.let { append(it) }
        entry.size_gb?.let {
            if (isNotEmpty()) append(" • ")
            append("%.1f GB".format(it))
        }
        if (entry.entry_type == "SERIES") {
            entry.seasons_detail?.let { detail ->
                if (isNotEmpty()) append(" • ")
                append(formatSeasonsDetail(detail))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = { onEntryClick(entry.id) },
                onLongClick = { showMenu = true },
            ),
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
                    .background(posterBrush),
            )
        }
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
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.background(CineSurfaceContainerHigh),
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = { showMenu = false; onEditEntry(entry.id) },
                leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = CineTertiary) },
            )
            DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                onClick = { showMenu = false; showDeleteConfirm = true },
                leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            )
        }

        if (icon != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .widthIn(max = 110.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.50f))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
                entry.first_device_name?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Entry") },
            text = { Text("Remove \"${entry.title}\" from your library? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    scope.launch { repository.deleteMediaEntry(entry.id) }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}
