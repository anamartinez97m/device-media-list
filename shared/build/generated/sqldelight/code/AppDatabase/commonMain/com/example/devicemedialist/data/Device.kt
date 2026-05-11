package com.example.devicemedialist.`data`

import kotlin.Double
import kotlin.String

public data class Device(
  public val id: String,
  public val name: String,
  public val type: String,
  public val used_storage_gb: Double?,
  public val total_storage_gb: Double?,
)
