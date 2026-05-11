package com.example.devicemedialist.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class PlatformSettingQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    platform: String,
    display_name: String,
    color_hex: String,
    enabled: Long,
  ) -> T): Query<T> = Query(-311_002_985, arrayOf("platform_setting"), driver, "PlatformSetting.sq",
      "selectAll",
      "SELECT platform_setting.platform, platform_setting.display_name, platform_setting.color_hex, platform_setting.enabled FROM platform_setting ORDER BY display_name ASC") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun selectAll(): Query<Platform_setting> = selectAll { platform, display_name, color_hex,
      enabled ->
    Platform_setting(
      platform,
      display_name,
      color_hex,
      enabled
    )
  }

  public fun <T : Any> selectEnabled(mapper: (
    platform: String,
    display_name: String,
    color_hex: String,
    enabled: Long,
  ) -> T): Query<T> = Query(-1_134_551_241, arrayOf("platform_setting"), driver,
      "PlatformSetting.sq", "selectEnabled",
      "SELECT platform_setting.platform, platform_setting.display_name, platform_setting.color_hex, platform_setting.enabled FROM platform_setting WHERE enabled = 1 ORDER BY display_name ASC") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun selectEnabled(): Query<Platform_setting> = selectEnabled { platform, display_name,
      color_hex, enabled ->
    Platform_setting(
      platform,
      display_name,
      color_hex,
      enabled
    )
  }

  public fun insertIfNotExists(
    platform: String,
    display_name: String,
    color_hex: String,
  ) {
    driver.execute(-487_622_773,
        """INSERT OR IGNORE INTO platform_setting(platform, display_name, color_hex) VALUES (?, ?, ?)""",
        3) {
          bindString(0, platform)
          bindString(1, display_name)
          bindString(2, color_hex)
        }
    notifyQueries(-487_622_773) { emit ->
      emit("platform_setting")
    }
  }

  public fun insert(
    platform: String,
    display_name: String,
    color_hex: String,
  ) {
    driver.execute(1_589_075_559,
        """INSERT OR REPLACE INTO platform_setting(platform, display_name, color_hex, enabled) VALUES (?, ?, ?, 1)""",
        3) {
          bindString(0, platform)
          bindString(1, display_name)
          bindString(2, color_hex)
        }
    notifyQueries(1_589_075_559) { emit ->
      emit("platform_setting")
    }
  }

  public fun delete(platform: String) {
    driver.execute(1_437_409_625, """DELETE FROM platform_setting WHERE platform = ?""", 1) {
          bindString(0, platform)
        }
    notifyQueries(1_437_409_625) { emit ->
      emit("platform_setting")
    }
  }

  public fun setEnabled(enabled: Long, platform: String) {
    driver.execute(1_969_432_877, """UPDATE platform_setting SET enabled = ? WHERE platform = ?""",
        2) {
          bindLong(0, enabled)
          bindString(1, platform)
        }
    notifyQueries(1_969_432_877) { emit ->
      emit("platform_setting")
    }
  }
}
