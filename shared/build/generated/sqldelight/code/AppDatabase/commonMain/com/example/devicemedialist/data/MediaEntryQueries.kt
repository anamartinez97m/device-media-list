package com.example.devicemedialist.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class MediaEntryQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAllWithFirstDevice(mapper: (
    id: String,
    title: String,
    entry_type: String?,
    release_year: Long?,
    genre: String?,
    platform: String,
    first_device_type: String?,
  ) -> T): Query<T> = Query(120_548_564, arrayOf("media_entry", "device", "media_entry_device"),
      driver, "MediaEntry.sq", "selectAllWithFirstDevice", """
  |SELECT
  |    me.id,
  |    me.title,
  |    me.entry_type,
  |    me.release_year,
  |    me.genre,
  |    me.platform,
  |    d.type AS first_device_type
  |FROM media_entry me
  |LEFT JOIN media_entry_device med ON med.entry_id = me.id
  |LEFT JOIN device d ON d.id = med.device_id
  |GROUP BY me.id
  |ORDER BY me.title ASC
  """.trimMargin()) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getString(4),
      cursor.getString(5)!!,
      cursor.getString(6)
    )
  }

  public fun selectAllWithFirstDevice(): Query<SelectAllWithFirstDevice> =
      selectAllWithFirstDevice { id, title, entry_type, release_year, genre, platform,
      first_device_type ->
    SelectAllWithFirstDevice(
      id,
      title,
      entry_type,
      release_year,
      genre,
      platform,
      first_device_type
    )
  }

  public fun insert(
    id: String,
    title: String,
    entry_type: String?,
    release_year: Long?,
    genre: String?,
    platform: String,
  ) {
    driver.execute(-1_949_112_046, """
        |INSERT OR REPLACE INTO media_entry(id, title, entry_type, release_year, genre, platform)
        |VALUES (?, ?, ?, ?, ?, ?)
        """.trimMargin(), 6) {
          bindString(0, id)
          bindString(1, title)
          bindString(2, entry_type)
          bindLong(3, release_year)
          bindString(4, genre)
          bindString(5, platform)
        }
    notifyQueries(-1_949_112_046) { emit ->
      emit("media_entry")
    }
  }

  public fun delete(id: String) {
    driver.execute(-2_100_777_980, """DELETE FROM media_entry WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(-2_100_777_980) { emit ->
      emit("media_entry")
      emit("media_entry_device")
    }
  }
}
