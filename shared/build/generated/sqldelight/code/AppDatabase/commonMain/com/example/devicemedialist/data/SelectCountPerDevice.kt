package com.example.devicemedialist.`data`

import kotlin.Long
import kotlin.String

public data class SelectCountPerDevice(
  public val device_id: String,
  public val entry_count: Long,
)
