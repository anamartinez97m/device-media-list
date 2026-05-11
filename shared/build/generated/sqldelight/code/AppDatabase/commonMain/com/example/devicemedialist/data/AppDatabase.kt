package com.example.devicemedialist.`data`

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.example.devicemedialist.`data`.shared.newInstance
import com.example.devicemedialist.`data`.shared.schema
import kotlin.Unit

public interface AppDatabase : Transacter {
  public val deviceQueries: DeviceQueries

  public val mediaEntryQueries: MediaEntryQueries

  public val mediaEntryDeviceQueries: MediaEntryDeviceQueries

  public val platformSettingQueries: PlatformSettingQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = AppDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): AppDatabase =
        AppDatabase::class.newInstance(driver)
  }
}
