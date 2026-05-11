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
import androidx.compose.material.icons.rounded.FilterList
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
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineOnTertiary
import com.example.devicemedialist.theme.CineSurfaceContainerHigh
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary

private enum class MediaSource(val label: String, val color: Color) {
    NETFLIX("NETFLIX", Color(0xFFE50914)),
    PRIME("PRIME", Color(0xFF00A8E1)),
    LOCAL("LOCAL", Color(0xFF2A2A2B)),
}

private enum class DeviceType(val icon: ImageVector) {
    PHONE(Icons.Rounded.PhoneAndroid),
    TABLET(Icons.Rounded.Tablet),
    TV(Icons.Rounded.Tv),
}

private data class MediaItem(
    val title: String,
    val genre: String,
    val year: Int,
    val source: MediaSource,
    val device: DeviceType,
    val posterBrush: Brush,
)

private val sampleMedia = listOf(
    MediaItem(
        title = "Desert Planet",
        genre = "Sci-Fi",
        year = 2024,
        source = MediaSource.NETFLIX,
        device = DeviceType.TV,
        posterBrush = Brush.verticalGradient(
            listOf(Color(0xFF1A0800), Color(0xFF6B2800), Color(0xFFD45A00), Color(0xFF0A0A0A))
        ),
    ),
    MediaItem(
        title = "The Woods",
        genre = "Thriller",
        year = 2023,
        source = MediaSource.PRIME,
        device = DeviceType.TV,
        posterBrush = Brush.verticalGradient(
            listOf(Color(0xFF050F05), Color(0xFF0D2B10), Color(0xFF1A3D1E), Color(0xFF0A0A0A))
        ),
    ),
    MediaItem(
        title = "Neon Run",
        genre = "Action",
        year = 2025,
        source = MediaSource.LOCAL,
        device = DeviceType.TV,
        posterBrush = Brush.verticalGradient(
            listOf(Color(0xFF05050F), Color(0xFF0D1035), Color(0xFF1A2060), Color(0xFF0A0A0A))
        ),
    ),
)

@Composable
fun HomeScreen(paddingValues: PaddingValues, onAddEntry: () -> Unit) {
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
            StorageSummaryCard(usedTb = 1.2f, totalTb = 2.0f)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AddNewEntryButton(onAddEntry = onAddEntry)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            LibrarySectionHeader()
        }
        items(sampleMedia) { media ->
            MediaCard(media)
        }
    }
}

@Composable
private fun StorageSummaryCard(usedTb: Float, totalTb: Float) {
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
                    text = "${usedTb} TB",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "of ${totalTb} TB Used",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CineOnSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { usedTb / totalTb },
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
private fun MediaCard(media: MediaItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .clip(MaterialTheme.shapes.medium),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(media.posterBrush),
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
                imageVector = media.device.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = media.source.label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(media.source.color)
                .padding(horizontal = 5.dp, vertical = 2.dp),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
        ) {
            Text(
                text = "${media.genre} • ${media.year}",
                style = MaterialTheme.typography.labelSmall,
                color = CineOnSurfaceVariant,
            )
            Text(
                text = media.title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
