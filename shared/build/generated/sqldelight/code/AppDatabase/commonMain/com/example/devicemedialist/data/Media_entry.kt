package com.example.devicemedialist.`data`

import kotlin.Double
import kotlin.Long
import kotlin.String

public data class Media_entry(
  public val id: String,
  public val title: String,
  public val entry_type: String?,
  public val release_year: Long?,
  public val size_gb: Double?,
  public val platform: String,
)
