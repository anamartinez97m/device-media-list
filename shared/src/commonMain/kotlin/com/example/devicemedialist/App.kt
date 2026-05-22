package com.example.devicemedialist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.devicemedialist.components.TopBar
import com.example.devicemedialist.data.AppRepository
import com.example.devicemedialist.data.BackHandler
import com.example.devicemedialist.data.BackupManager
import com.example.devicemedialist.screens.AddEntryScreen
import com.example.devicemedialist.screens.DevicesScreen
import com.example.devicemedialist.screens.EditEntryScreen
import com.example.devicemedialist.screens.EntryDetailScreen
import com.example.devicemedialist.screens.HomeScreen
import com.example.devicemedialist.screens.SettingsScreen
import com.example.devicemedialist.theme.CineGlassBorder
import com.example.devicemedialist.theme.CineOnSurfaceVariant
import com.example.devicemedialist.theme.CineSurfaceContainerLow
import com.example.devicemedialist.theme.CineTertiary
import com.example.devicemedialist.theme.CineTrackTheme

val LocalRepository = staticCompositionLocalOf<AppRepository> {
    error("No AppRepository provided")
}

val LocalBackupManager = staticCompositionLocalOf<BackupManager> {
    error("No BackupManager provided")
}

private data class NavTab(val label: String, val icon: ImageVector)

private val navTabs = listOf(
    NavTab("Home", Icons.Rounded.GridView),
    NavTab("Devices", Icons.Rounded.Devices),
    NavTab("Settings", Icons.Rounded.Settings),
)

@Composable
fun App(repository: AppRepository, backupManager: BackupManager) {
    CompositionLocalProvider(
        LocalRepository provides repository,
        LocalBackupManager provides backupManager,
    ) {
    CineTrackTheme {
        var selectedTab by remember { mutableIntStateOf(0) }
        var showAddEntry by remember { mutableStateOf(false) }
        var selectedEntryId by remember { mutableStateOf<String?>(null) }
        var editingEntryId by remember { mutableStateOf<String?>(null) }
        var isSearchActive by remember { mutableStateOf(false) }
        var searchQuery by remember { mutableStateOf("") }

        BackHandler(
            enabled = isSearchActive || editingEntryId != null || selectedEntryId != null || showAddEntry,
        ) {
            when {
                isSearchActive -> { isSearchActive = false; searchQuery = "" }
                editingEntryId != null -> editingEntryId = null
                selectedEntryId != null -> selectedEntryId = null
                showAddEntry -> showAddEntry = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopBar(
                    showSearch = selectedTab == 0,
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onSearchToggle = { isSearchActive = !isSearchActive; if (!isSearchActive) searchQuery = "" },
                    onQueryChange = { searchQuery = it },
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = CineGlassBorder,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx(),
                            )
                        },
                    containerColor = CineSurfaceContainerLow.copy(alpha = 0.92f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    navTabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index; isSearchActive = false; searchQuery = "" },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CineTertiary,
                                selectedTextColor = CineTertiary,
                                unselectedIconColor = CineOnSurfaceVariant,
                                unselectedTextColor = CineOnSurfaceVariant,
                                indicatorColor = Color.Transparent,
                            ),
                        )
                    }
                }
            },
        ) { paddingValues ->
            when (selectedTab) {
                0 -> HomeScreen(
                    paddingValues = paddingValues,
                    onAddEntry = { showAddEntry = true },
                    onEntryClick = { selectedEntryId = it },
                    onEditEntry = { editingEntryId = it },
                    searchQuery = searchQuery,
                )
                1 -> DevicesScreen(paddingValues)
                2 -> SettingsScreen(paddingValues, backupManager = backupManager)
            }
        }

        if (showAddEntry) {
            AddEntryScreen(
                onCancel = { showAddEntry = false },
                onSave = { showAddEntry = false },
            )
        }
        selectedEntryId?.let { entryId ->
            EntryDetailScreen(
                entryId = entryId,
                onClose = { selectedEntryId = null },
                onEdit = { id -> selectedEntryId = null; editingEntryId = id },
            )
        }
        editingEntryId?.let { entryId ->
            EditEntryScreen(
                entryId = entryId,
                onCancel = { editingEntryId = null },
                onSave = { editingEntryId = null },
            )
        }
        }
    }
    }
}
