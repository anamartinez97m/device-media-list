package com.example.devicemedialist.`data`.shared

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.example.devicemedialist.`data`.AppDatabase
import com.example.devicemedialist.`data`.DeviceQueries
import com.example.devicemedialist.`data`.MediaEntryDeviceQueries
import com.example.devicemedialist.`data`.MediaEntryQueries
import com.example.devicemedialist.`data`.PlatformSettingQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<AppDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = AppDatabaseImpl.Schema

internal fun KClass<AppDatabase>.newInstance(driver: SqlDriver): AppDatabase =
    AppDatabaseImpl(driver)

private class AppDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), AppDatabase {
  override val deviceQueries: DeviceQueries = DeviceQueries(driver)

  override val mediaEntryQueries: MediaEntryQueries = MediaEntryQueries(driver)

  override val mediaEntryDeviceQueries: MediaEntryDeviceQueries = MediaEntryDeviceQueries(driver)

  override val platformSettingQueries: PlatformSettingQueries = PlatformSettingQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 3

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE device (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    name TEXT NOT NULL,
          |    type TEXT NOT NULL,
          |    used_storage_gb REAL,
          |    total_storage_gb REAL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE media_entry (
          |    id TEXT NOT NULL PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    entry_type TEXT,
          |    release_year INTEGER,
          |    size_gb REAL,
          |    platform TEXT NOT NULL,
          |    seasons_detail TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE media_entry_device (
          |    entry_id TEXT NOT NULL,
          |    device_id TEXT NOT NULL,
          |    PRIMARY KEY (entry_id, device_id),
          |    FOREIGN KEY (entry_id) REFERENCES media_entry(id) ON DELETE CASCADE,
          |    FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE platform_setting (
          |    platform TEXT NOT NULL PRIMARY KEY,
          |    display_name TEXT NOT NULL,
          |    color_hex TEXT NOT NULL,
          |    enabled INTEGER NOT NULL DEFAULT 1
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    private fun migrateInternal(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
    ): QueryResult.Value<Unit> {
      if (oldVersion <= 1 && newVersion > 1) {
        driver.execute(null, """
            |CREATE TABLE IF NOT EXISTS platform_setting (
            |    platform TEXT NOT NULL PRIMARY KEY,
            |    enabled INTEGER NOT NULL DEFAULT 1
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |CREATE TABLE platform_setting_new (
            |    platform TEXT NOT NULL PRIMARY KEY,
            |    display_name TEXT NOT NULL,
            |    color_hex TEXT NOT NULL,
            |    enabled INTEGER NOT NULL DEFAULT 1
            |)
            """.trimMargin(), 0)
        driver.execute(null, """
            |INSERT INTO platform_setting_new(platform, display_name, color_hex, enabled)
            |SELECT
            |    platform,
            |    CASE platform
            |        WHEN 'NETFLIX' THEN 'Netflix'
            |        WHEN 'PRIME'   THEN 'Prime'
            |        WHEN 'DISNEY'  THEN 'Disney+'
            |        WHEN 'LOCAL'   THEN 'Local'
            |        ELSE platform
            |    END,
            |    CASE platform
            |        WHEN 'NETFLIX' THEN '#E50914'
            |        WHEN 'PRIME'   THEN '#00A8E1'
            |        WHEN 'DISNEY'  THEN '#1B4FA8'
            |        WHEN 'LOCAL'   THEN '#4A4A5A'
            |        ELSE '#4A4A5A'
            |    END,
            |    enabled
            |FROM platform_setting
            """.trimMargin(), 0)
        driver.execute(null, "DROP TABLE IF EXISTS platform_setting", 0)
        driver.execute(null, "ALTER TABLE platform_setting_new RENAME TO platform_setting", 0)
      }
      if (oldVersion <= 2 && newVersion > 2) {
        driver.execute(null, "ALTER TABLE media_entry ADD COLUMN seasons_detail TEXT", 0)
      }
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> {
      var lastVersion = oldVersion

      callbacks.filter { it.afterVersion in oldVersion until newVersion }
      .sortedBy { it.afterVersion }
      .forEach { callback ->
        migrateInternal(driver, oldVersion = lastVersion, newVersion = callback.afterVersion + 1)
        callback.block(driver)
        lastVersion = callback.afterVersion + 1
      }

      if (lastVersion < newVersion) {
        migrateInternal(driver, lastVersion, newVersion)
      }
      return QueryResult.Unit
    }
  }
}
