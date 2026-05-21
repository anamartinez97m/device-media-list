package com.example.devicemedialist.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Double
import kotlin.String

public class DeviceQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> selectAll(mapper: (
    id: String,
    name: String,
    type: String,
    used_storage_gb: Double?,
    total_storage_gb: Double?,
  ) -> T): Query<T> = Query(-2_119_690_972, arrayOf("device"), driver, "Device.sq", "selectAll",
      "SELECT device.id, device.name, device.type, device.used_storage_gb, device.total_storage_gb FROM device") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getDouble(3),
      cursor.getDouble(4)
    )
  }

  public fun selectAll(): Query<Device> = selectAll { id, name, type, used_storage_gb,
      total_storage_gb ->
    Device(
      id,
      name,
      type,
      used_storage_gb,
      total_storage_gb
    )
  }

  public fun <T : Any> selectForEntry(entry_id: String, mapper: (
    id: String,
    name: String,
    type: String,
    used_storage_gb: Double?,
    total_storage_gb: Double?,
  ) -> T): Query<T> = SelectForEntryQuery(entry_id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getDouble(3),
      cursor.getDouble(4)
    )
  }

  public fun selectForEntry(entry_id: String): Query<Device> = selectForEntry(entry_id) { id, name,
      type, used_storage_gb, total_storage_gb ->
    Device(
      id,
      name,
      type,
      used_storage_gb,
      total_storage_gb
    )
  }

  public fun insert(
    id: String,
    name: String,
    type: String,
    used_storage_gb: Double?,
    total_storage_gb: Double?,
  ) {
    driver.execute(-1_672_686_342, """
        |INSERT OR REPLACE INTO device(id, name, type, used_storage_gb, total_storage_gb)
        |VALUES (?, ?, ?, ?, ?)
        """.trimMargin(), 5) {
          bindString(0, id)
          bindString(1, name)
          bindString(2, type)
          bindDouble(3, used_storage_gb)
          bindDouble(4, total_storage_gb)
        }
    notifyQueries(-1_672_686_342) { emit ->
      emit("device")
    }
  }

  public fun update(
    name: String,
    type: String,
    used_storage_gb: Double?,
    total_storage_gb: Double?,
    id: String,
  ) {
    driver.execute(-1_327_740_150,
        """UPDATE device SET name = ?, type = ?, used_storage_gb = ?, total_storage_gb = ? WHERE id = ?""",
        5) {
          bindString(0, name)
          bindString(1, type)
          bindDouble(2, used_storage_gb)
          bindDouble(3, total_storage_gb)
          bindString(4, id)
        }
    notifyQueries(-1_327_740_150) { emit ->
      emit("device")
    }
  }

  public fun addUsedStorage(used_storage_gb: Double, id: String) {
    driver.execute(-1_334_697_506,
        """UPDATE device SET used_storage_gb = COALESCE(used_storage_gb, 0.0) + ? WHERE id = ? AND total_storage_gb IS NOT NULL""",
        2) {
          bindDouble(0, used_storage_gb)
          bindString(1, id)
        }
    notifyQueries(-1_334_697_506) { emit ->
      emit("device")
    }
  }

  public fun subtractUsedStorage(used_storage_gb: Double, id: String) {
    driver.execute(1_781_245_865,
        """UPDATE device SET used_storage_gb = COALESCE(used_storage_gb, 0.0) - ? WHERE id = ? AND total_storage_gb IS NOT NULL""",
        2) {
          bindDouble(0, used_storage_gb)
          bindString(1, id)
        }
    notifyQueries(1_781_245_865) { emit ->
      emit("device")
    }
  }

  public fun delete(id: String) {
    driver.execute(-1_824_352_276, """DELETE FROM device WHERE id = ?""", 1) {
          bindString(0, id)
        }
    notifyQueries(-1_824_352_276) { emit ->
      emit("device")
      emit("media_entry_device")
    }
  }

  private inner class SelectForEntryQuery<out T : Any>(
    public val entry_id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("device", "media_entry_device", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("device", "media_entry_device", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(249_642_886, """
    |SELECT device.id, device.name, device.type, device.used_storage_gb, device.total_storage_gb FROM device WHERE id IN (
    |    SELECT device_id FROM media_entry_device WHERE entry_id = ?
    |)
    """.trimMargin(), mapper, 1) {
      bindString(0, entry_id)
    }

    override fun toString(): String = "Device.sq:selectForEntry"
  }
}
