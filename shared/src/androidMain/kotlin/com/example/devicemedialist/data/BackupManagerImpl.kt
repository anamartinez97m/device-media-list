package com.example.devicemedialist.data

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.devicemedialist.worker.BackupWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class BackupManagerImpl(private val context: Context) : BackupManager {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(BackupWorker.PREFS_NAME, Context.MODE_PRIVATE)

    private val _lastBackupMs = MutableStateFlow<Long?>(
        prefs.getLong(BackupWorker.KEY_LAST_BACKUP, -1L).takeIf { it > 0 }
    )
    override val lastBackupMs: StateFlow<Long?> = _lastBackupMs.asStateFlow()

    private val _schedule = MutableStateFlow(
        BackupSchedule.valueOf(
            prefs.getString(KEY_SCHEDULE, BackupSchedule.DAILY.name) ?: BackupSchedule.DAILY.name
        )
    )
    override val schedule: StateFlow<BackupSchedule> = _schedule.asStateFlow()

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            BackupWorker.KEY_LAST_BACKUP ->
                _lastBackupMs.value = prefs.getLong(BackupWorker.KEY_LAST_BACKUP, -1L).takeIf { it > 0 }
            KEY_SCHEDULE ->
                _schedule.value = BackupSchedule.valueOf(
                    prefs.getString(KEY_SCHEDULE, BackupSchedule.DAILY.name) ?: BackupSchedule.DAILY.name
                )
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(prefListener)
        applySchedule(_schedule.value)
    }

    override fun setSchedule(schedule: BackupSchedule) {
        prefs.edit().putString(KEY_SCHEDULE, schedule.name).apply()
        _schedule.value = schedule
        applySchedule(schedule)
    }

    override fun backupNow() {
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<BackupWorker>().build())
    }

    override fun backupDirectoryPath(): String =
        context.getExternalFilesDir("backups")?.absolutePath ?: "Unavailable"

    override fun formatLastBackupTime(ms: Long): String {
        val now = System.currentTimeMillis()
        val diffMs = now - ms
        val diffMinutes = diffMs / 60_000
        val diffHours = diffMs / 3_600_000
        val diffDays = diffMs / 86_400_000
        return when {
            diffMinutes < 2 -> "Just now"
            diffHours < 1 -> "$diffMinutes min ago"
            diffHours < 24 -> {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
                "Today at $time"
            }
            diffDays == 1L -> {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ms))
                "Yesterday at $time"
            }
            else -> SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(ms))
        }
    }

    private fun applySchedule(schedule: BackupSchedule) {
        val wm = WorkManager.getInstance(context)
        when (schedule) {
            BackupSchedule.DISABLED -> wm.cancelUniqueWork(WORK_NAME)
            BackupSchedule.DAILY -> schedulePeriodicBackup(wm, 1L)
            BackupSchedule.WEEKLY -> schedulePeriodicBackup(wm, 7L)
        }
    }

    private fun schedulePeriodicBackup(wm: WorkManager, intervalDays: Long) {
        val request = PeriodicWorkRequestBuilder<BackupWorker>(intervalDays, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().build())
            .build()
        wm.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    companion object {
        private const val WORK_NAME = "cinetrack_backup"
        private const val KEY_SCHEDULE = "backup_schedule"
    }
}
