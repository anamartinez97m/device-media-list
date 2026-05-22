package com.example.devicemedialist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.devicemedialist.data.AppRepository
import com.example.devicemedialist.data.BackupManagerImpl
import com.example.devicemedialist.data.DatabaseDriverFactory
import com.example.devicemedialist.data.createAppDatabase

class MainActivity : ComponentActivity() {

    private val repository: AppRepository by lazy {
        val driverFactory = DatabaseDriverFactory(applicationContext)
        val database = createAppDatabase(driverFactory)
        AppRepository(database)
    }

    private val backupManager: BackupManagerImpl by lazy {
        BackupManagerImpl(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        setContent {
            App(repository, backupManager)
        }
    }
}
