package com.example.devicemedialist.`data`

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class MediaEntryDeviceQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun selectDeviceIdsForEntry(entry_id: String): Query<String> =
      SelectDeviceIdsForEntryQuery(entry_id) { cursor ->
    cursor.getString(0)!!
  }

  public fun <T : Any> selectCountPerDevice(mapper: (device_id: String, entry_count: Long) -> T):
      Query<T> = Query(-1_774_134_161, arrayOf("media_entry_device"), driver, "MediaEntryDevice.sq",
      "selectCountPerDevice",
      "SELECT device_id, COUNT(*) AS entry_count FROM media_entry_device GROUP BY device_id") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!
    )
  }

  public fun selectCountPerDevice(): Query<SelectCountPerDevice> = selectCountPerDevice { device_id,
      entry_count ->
    SelectCountPerDevice(
      device_id,
      entry_count
    )
  }

  public fun insertLink(entry_id: String, device_id: String) {
    driver.execute(-1_995_813_918,
        """INSERT OR IGNORE INTO media_entry_device(entry_id, device_id) VALUES (?, ?)""", 2) {
          bindString(0, entry_id)
          bindString(1, device_id)
        }
    notifyQueries(-1_995_813_918) { emit ->
      emit("media_entry_device")
    }
  }

  public fun deleteForEntry(entry_id: String) {
    driver.execute(724_580_867, """DELETE FROM media_entry_device WHERE entry_id = ?""", 1) {
          bindString(0, entry_id)
        }
    notifyQueries(724_580_867) { emit ->
      emit("media_entry_device")
    }
  }

  public fun deleteForDevice(device_id: String) {
    driver.execute(950_279_909, """DELETE FROM media_entry_device WHERE device_id = ?""", 1) {
          bindString(0, device_id)
        }
    notifyQueries(950_279_909) { emit ->
      emit("media_entry_device")
    }
  }

  private inner class SelectDeviceIdsForEntryQuery<out T : Any>(
    public val entry_id: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("media_entry_device", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("media_entry_device", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-50_055_936,
        """SELECT device_id FROM media_entry_device WHERE entry_id = ?""", mapper, 1) {
      bindString(0, entry_id)
    }

    override fun toString(): String = "MediaEntryDevice.sq:selectDeviceIdsForEntry"
  }
}
