package com.example.devicemedialist.data

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

fun createAppDatabase(driverFactory: DatabaseDriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(driver)
}
