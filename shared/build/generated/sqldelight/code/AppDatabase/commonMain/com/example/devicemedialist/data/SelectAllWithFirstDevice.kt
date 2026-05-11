package com.example.devicemedialist.`data`

import kotlin.Long
import kotlin.String

public data class SelectAllWithFirstDevice(
  public val id: String,
  public val title: String,
  public val entry_type: String?,
  public val release_year: Long?,
  public val genre: String?,
  public val platform: String,
  public val first_device_type: String?,
)
