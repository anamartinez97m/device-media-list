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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Tablet
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

private enum class DeviceStatus(val label: String, val color: Color) {
    ONLINE_SYNCING("Online • Syncing", Color(0xFF4CAF50)),
    OFFLINE("Offline", Color(0xFF757575)),
    ONLINE_STREAMING("Online • Streaming", Color(0xFF4CAF50)),
}

private data class DeviceItem(
    val name: String,
    val status: DeviceStatus,
    val icon: ImageVector,
    val usedGb: Int?,
    val totalGb: Int?,
    val titlesDownloaded: Int,
)

private val sampleDevices = listOf(
    DeviceItem(
        name = "My Phone",
        status = DeviceStatus.ONLINE_SYNCING,
        icon = Icons.Rounded.PhoneAndroid,
        usedGb = 45,
        totalGb = 128,
        titlesDownloaded = 24,
    ),
    DeviceItem(
        name = "Work Tablet",
        status = DeviceStatus.OFFLINE,
        icon = Icons.Rounded.Tablet,
        usedGb = 112,
        totalGb = 256,
        titlesDownloaded = 56,
    ),
    DeviceItem(
        name = "Home TV",
        status = DeviceStatus.ONLINE_STREAMING,
        icon = Icons.Rounded.Tv,
        usedGb = null,
        totalGb = null,
        titlesDownloaded = 0,
    ),
)

@Composable
fun DevicesScreen(paddingValues: PaddingValues) {
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
        item { DevicesHeader() }
        items(sampleDevices) { device ->
            DeviceCard(device)
        }
    }
}

@Composable
private fun DevicesHeader() {
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
            onClick = {},
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
private fun DeviceCard(device: DeviceItem) {
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
                    imageVector = device.icon,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(device.status.color),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = device.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = device.status.color,
                    )
                }
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More options",
                    tint = CineOnSurfaceVariant,
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
                text = if (device.usedGb != null && device.totalGb != null)
                    "${device.usedGb}GB / ${device.totalGb}GB"
                else
                    "Streaming Only",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (device.usedGb != null && device.totalGb != null) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { device.usedGb.toFloat() / device.totalGb.toFloat() },
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
                tint = if (device.titlesDownloaded > 0) CineTertiary else CineOnSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${device.titlesDownloaded} Titles Downloaded",
                style = MaterialTheme.typography.labelLarge,
                color = CineOnSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {},
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                Text(
                    text = "MANAGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CineTertiary,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
