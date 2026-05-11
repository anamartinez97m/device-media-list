package com.example.devicemedialist.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CineDarkColorScheme = darkColorScheme(
    primary = CinePrimary,
    onPrimary = CineOnPrimary,
    primaryContainer = CinePrimaryContainer,
    onPrimaryContainer = CineOnPrimaryContainer,
    inversePrimary = CineInversePrimary,
    secondary = CineSecondary,
    onSecondary = CineOnSecondary,
    secondaryContainer = CineSecondaryContainer,
    onSecondaryContainer = CineOnSecondaryContainer,
    tertiary = CineTertiary,
    onTertiary = CineOnTertiary,
    tertiaryContainer = CineTertiaryContainer,
    onTertiaryContainer = CineOnTertiaryContainer,
    error = CineError,
    onError = CineOnError,
    errorContainer = CineErrorContainer,
    onErrorContainer = CineOnErrorContainer,
    background = CineBackground,
    onBackground = CineOnBackground,
    surface = CineSurface,
    onSurface = CineOnSurface,
    surfaceVariant = CineSurfaceVariant,
    onSurfaceVariant = CineOnSurfaceVariant,
    inverseSurface = CineInverseSurface,
    inverseOnSurface = CineInverseOnSurface,
    outline = CineOutline,
    outlineVariant = CineOutlineVariant,
    surfaceTint = CineSurfaceTint,
)

@Composable
fun CineTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CineDarkColorScheme,
        typography = CineTypography,
        shapes = CineShapes,
        content = content,
    )
}
