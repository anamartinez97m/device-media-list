package com.example.devicemedialist.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class StorageSummary(val usedGb: Float, val totalGb: Float)

private fun generateId(): String {
    val hex = "0123456789abcdef"
    return buildString {
        repeat(8) { append(hex[Random.nextInt(16)]) }; append('-')
        repeat(4) { append(hex[Random.nextInt(16)]) }; append('-')
        append('4')
        repeat(3) { append(hex[Random.nextInt(16)]) }; append('-')
        append(hex[8 + Random.nextInt(4)])
        repeat(3) { append(hex[Random.nextInt(16)]) }; append('-')
        repeat(12) { append(hex[Random.nextInt(16)]) }
    }
}

private data class PlatformDefault(val key: String, val name: String, val colorHex: String)

private val DEFAULT_PLATFORMS = listOf(
    PlatformDefault("NETFLIX", "Netflix", "#E50914"),
    PlatformDefault("PRIME", "Prime", "#00A8E1"),
    PlatformDefault("DISNEY", "Disney+", "#1B4FA8"),
    PlatformDefault("LOCAL", "Local", "#4A4A5A"),
)

class AppRepository(private val database: AppDatabase) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            DEFAULT_PLATFORMS.forEach { p ->
                database.platformSettingQueries.insertIfNotExists(p.key, p.name, p.colorHex)
            }
        }
    }

    val devices: StateFlow<List<Device>> = database.deviceQueries
        .selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val mediaEntries: StateFlow<List<SelectAllWithFirstDevice>> = database.mediaEntryQueries
        .selectAllWithFirstDevice()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val entriesPerDevice: StateFlow<Map<String, Long>> = database.mediaEntryDeviceQueries
        .selectCountPerDevice()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows -> rows.associate { it.device_id to it.entry_count } }
        .stateIn(scope, SharingStarted.Eagerly, emptyMap())

    val allPlatforms: StateFlow<List<Platform_setting>> = database.platformSettingQueries
        .selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val enabledPlatforms: StateFlow<List<Platform_setting>> = database.platformSettingQueries
        .selectEnabled()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    val storageSummary: StateFlow<StorageSummary> = devices
        .map { list ->
            val used = list.sumOf { it.used_storage_gb ?: 0.0 }
            val total = list.sumOf { it.total_storage_gb ?: 0.0 }
            StorageSummary(used.toFloat(), total.toFloat())
        }
        .stateIn(scope, SharingStarted.Eagerly, StorageSummary(0f, 0f))

    suspend fun insertDevice(
        name: String,
        type: String,
        usedStorageGb: Double?,
        totalStorageGb: Double?,
    ) = withContext(Dispatchers.IO) {
        database.deviceQueries.insert(
            id = generateId(),
            name = name,
            type = type,
            used_storage_gb = usedStorageGb,
            total_storage_gb = totalStorageGb,
        )
    }

    suspend fun deleteDevice(id: String) = withContext(Dispatchers.IO) {
        database.transaction {
            database.mediaEntryDeviceQueries.deleteForDevice(id)
            database.deviceQueries.delete(id)
        }
    }

    suspend fun insertMediaEntry(
        title: String,
        platform: String,
        entryType: String?,
        releaseYear: Long?,
        genre: String?,
        deviceIds: List<String>,
    ) = withContext(Dispatchers.IO) {
        val id = generateId()
        database.transaction {
            database.mediaEntryQueries.insert(
                id = id,
                title = title,
                entry_type = entryType,
                release_year = releaseYear,
                genre = genre,
                platform = platform,
            )
            deviceIds.forEach { deviceId ->
                database.mediaEntryDeviceQueries.insertLink(
                    entry_id = id,
                    device_id = deviceId,
                )
            }
        }
    }

    suspend fun insertPlatform(displayName: String, colorHex: String) = withContext(Dispatchers.IO) {
        val key = displayName.trim().uppercase().replace(Regex("\\s+"), "_")
        database.platformSettingQueries.insert(key, displayName.trim(), colorHex)
    }

    suspend fun deletePlatform(key: String) = withContext(Dispatchers.IO) {
        database.platformSettingQueries.delete(key)
    }

    suspend fun setPlatformEnabled(platform: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        database.platformSettingQueries.setEnabled(if (enabled) 1L else 0L, platform)
    }

    suspend fun deleteMediaEntry(id: String) = withContext(Dispatchers.IO) {
        database.transaction {
            database.mediaEntryDeviceQueries.deleteForEntry(id)
            database.mediaEntryQueries.delete(id)
        }
    }
}
