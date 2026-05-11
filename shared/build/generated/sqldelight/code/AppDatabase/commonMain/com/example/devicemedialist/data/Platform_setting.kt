package com.example.devicemedialist.`data`

import kotlin.Long
import kotlin.String

public data class Platform_setting(
  public val platform: String,
  public val display_name: String,
  public val color_hex: String,
  public val enabled: Long,
)
