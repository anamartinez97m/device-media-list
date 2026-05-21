package com.example.devicemedialist.data

import kotlinx.coroutines.flow.StateFlow

enum class BackupSchedule { DISABLED, DAILY, WEEKLY }

interface BackupManager {
    val lastBackupMs: StateFlow<Long?>
    val schedule: StateFlow<BackupSchedule>
    fun setSchedule(schedule: BackupSchedule)
    fun backupNow()
    fun formatLastBackupTime(ms: Long): String
    fun backupDirectoryPath(): String
}
