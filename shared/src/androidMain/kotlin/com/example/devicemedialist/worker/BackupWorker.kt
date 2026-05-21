package com.example.devicemedialist.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupWorker(
    private val appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val dbFile = appContext.getDatabasePath(DB_NAME)
            if (!dbFile.exists()) return Result.failure()

            val backupDir = appContext.getExternalFilesDir("backups") ?: return Result.failure()
            backupDir.mkdirs()

            val stamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
            val dest = File(backupDir, "cinetrack_$stamp.db")
            dbFile.copyTo(dest, overwrite = true)

            val walFile = File(dbFile.path + "-wal")
            if (walFile.exists()) walFile.copyTo(File(backupDir, "cinetrack_$stamp.db-wal"), overwrite = true)

            val shmFile = File(dbFile.path + "-shm")
            if (shmFile.exists()) shmFile.copyTo(File(backupDir, "cinetrack_$stamp.db-shm"), overwrite = true)

            pruneOldBackups(backupDir)

            appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(KEY_LAST_BACKUP, System.currentTimeMillis())
                .apply()

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private fun pruneOldBackups(backupDir: File) {
        val mainBackups = backupDir.listFiles { f -> f.name.endsWith(".db") && !f.name.endsWith("-wal") && !f.name.endsWith("-shm") }
            ?.sortedBy { it.lastModified() } ?: return
        if (mainBackups.size > MAX_BACKUPS) {
            mainBackups.take(mainBackups.size - MAX_BACKUPS).forEach { old ->
                old.delete()
                File(old.path + "-wal").delete()
                File(old.path + "-shm").delete()
            }
        }
    }

    companion object {
        const val DB_NAME = "cinetrack.db"
        const val PREFS_NAME = "backup_prefs"
        const val KEY_LAST_BACKUP = "last_backup"
        private const val MAX_BACKUPS = 5
    }
}
