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
    image_uri: String?,
    first_device_type: String?,
    first_device_name: String?,
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
  |    me.image_uri,
  |    d.type AS first_device_type,
  |    d.name AS first_device_name
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
      cursor.getString(7),
      cursor.getString(8),
      cursor.getString(9)
    )
  }

  public fun selectAllWithFirstDevice(): Query<SelectAllWithFirstDevice> =
      selectAllWithFirstDevice { id, title, entry_type, release_year, size_gb, platform,
      seasons_detail, image_uri, first_device_type, first_device_name ->
    SelectAllWithFirstDevice(
      id,
      title,
      entry_type,
      release_year,
      size_gb,
      platform,
      seasons_detail,
      image_uri,
      first_device_type,
      first_device_name
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
    image_uri: String?,
  ) {
    driver.execute(-1_949_112_046, """
        |INSERT OR REPLACE INTO media_entry(id, title, entry_type, release_year, size_gb, platform, seasons_detail, image_uri)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 8) {
          bindString(0, id)
          bindString(1, title)
          bindString(2, entry_type)
          bindLong(3, release_year)
          bindDouble(4, size_gb)
          bindString(5, platform)
          bindString(6, seasons_detail)
          bindString(7, image_uri)
        }
    notifyQueries(-1_949_112_046) { emit ->
      emit("media_entry")
    }
  }

  public fun update(
    title: String,
    entry_type: String?,
    release_year: Long?,
    size_gb: Double?,
    platform: String,
    seasons_detail: String?,
    image_uri: String?,
    id: String,
  ) {
    driver.execute(-1_604_165_854, """
        |UPDATE media_entry
        |SET title = ?, entry_type = ?, release_year = ?, size_gb = ?, platform = ?, seasons_detail = ?, image_uri = ?
        |WHERE id = ?
        """.trimMargin(), 8) {
          bindString(0, title)
          bindString(1, entry_type)
          bindLong(2, release_year)
          bindDouble(3, size_gb)
          bindString(4, platform)
          bindString(5, seasons_detail)
          bindString(6, image_uri)
          bindString(7, id)
        }
    notifyQueries(-1_604_165_854) { emit ->
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
