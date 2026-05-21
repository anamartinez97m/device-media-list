package com.example.devicemedialist.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
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
    size_gb: Double?,
    platform: String,
    seasons_detail: String?,
    first_device_type: String?,
  ) -> T): Query<T> = Query(120_548_564, arrayOf("media_entry", "device", "media_entry_device"),
      driver, "MediaEntry.sq", "selectAllWithFirstDevice", """
  |SELECT
  |    me.id,
  |    me.title,
  |    me.entry_type,
  |    me.release_year,
  |    me.size_gb,
  |    me.platform,
  |    me.seasons_detail,
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
      cursor.getDouble(4),
      cursor.getString(5)!!,
      cursor.getString(6),
      cursor.getString(7)
    )
  }

  public fun selectAllWithFirstDevice(): Query<SelectAllWithFirstDevice> =
      selectAllWithFirstDevice { id, title, entry_type, release_year, size_gb, platform,
      seasons_detail, first_device_type ->
    SelectAllWithFirstDevice(
      id,
      title,
      entry_type,
      release_year,
      size_gb,
      platform,
      seasons_detail,
      first_device_type
    )
  }

  public fun <T : Any> selectSizeById(id: String, mapper: (size_gb: Double?) -> T): Query<T> =
      SelectSizeByIdQuery(id) { cursor ->
    mapper(
      cursor.getDouble(0)
    )
  }

  public fun selectSizeById(id: String): Query<SelectSizeById> = selectSizeById(id) { size_gb ->
    SelectSizeById(
      size_gb
    )
  }

  public fun insert(
    id: String,
    title: String,
    entry_type: String?,
    release_year: Long?,
    size_gb: Double?,
    platform: String,
    seasons_detail: String?,
  ) {
    driver.execute(-1_949_112_046, """
        |INSERT OR REPLACE INTO media_entry(id, title, entry_type, release_year, size_gb, platform, seasons_detail)
        |VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 7) {
          bindString(0, id)
          bindString(1, title)
          bindString(2, entry_type)
          bindLong(3, release_year)
          bindDouble(4, size_gb)
          bindString(5, platform)
          bindString(6, seasons_detail)
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

  private inner class SelectSizeByIdQuery<out T : Any>(
    public val id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("media_entry", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("media_entry", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_876_779_496, """SELECT size_gb FROM media_entry WHERE id = ?""",
        mapper, 1) {
      bindString(0, id)
    }

    override fun toString(): String = "MediaEntry.sq:selectSizeById"
  }
}
