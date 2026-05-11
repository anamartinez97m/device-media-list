package com.example.devicemedialist.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape tokens mapped from the design spec:
 *   sm      → 0.25rem (4px)   — tiny badges
 *   DEFAULT → 0.5rem  (8px)   — small elements
 *   md      → 0.75rem (12px)  — media cards (primary)
 *   lg      → 1rem    (16px)  — container cards
 *   xl      → 1.5rem  (24px)  — large surfaces
 *   full    → 9999px          — buttons / search bars → use CircleShape
 */
val CineShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

val CineShapeCircle = CircleShape
