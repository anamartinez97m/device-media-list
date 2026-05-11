package com.example.devicemedialist.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Typefaces:
 *   Headlines → Spline Sans (geometric, modern)
 *   Body/Labels → Be Vietnam Pro (warm, readable)
 *
 * TODO: Replace [FontFamily.SansSerif] with downloaded font resources once
 *       font files are added to shared/src/commonMain/composeResources/font/.
 */
private val HeadlineFont = FontFamily.SansSerif
private val BodyFont = FontFamily.SansSerif

val CineTypography = Typography(
    // headline-xl — 40 / Bold / lh 48 / ls -0.02em
    displayLarge = TextStyle(
        fontFamily = HeadlineFont,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 48.sp,
        letterSpacing = (-0.02f).em,
    ),
    // headline-lg — 32 / SemiBold / lh 40
    headlineLarge = TextStyle(
        fontFamily = HeadlineFont,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    // headline-md — 24 / SemiBold / lh 32
    headlineSmall = TextStyle(
        fontFamily = HeadlineFont,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    // body-lg — 18 / Regular / lh 28
    bodyLarge = TextStyle(
        fontFamily = BodyFont,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    // body-md — 16 / Regular / lh 24
    bodyMedium = TextStyle(
        fontFamily = BodyFont,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    // label-md — 14 / Medium / lh 20
    labelLarge = TextStyle(
        fontFamily = BodyFont,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    // label-caps — 12 / Bold / lh 16 / ls +0.05em — uppercase tracking
    labelSmall = TextStyle(
        fontFamily = BodyFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
        letterSpacing = 0.05f.em,
    ),
)
