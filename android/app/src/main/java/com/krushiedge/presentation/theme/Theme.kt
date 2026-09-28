package com.krushiedge.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * KrushiEdge AI Theme
 *
 * Agricultural-inspired Material3 theme.
 * Supports light and dark modes.
 */

private val LightColorScheme = lightColorScheme(
    primary = KrushiColors.Primary,
    onPrimary = KrushiColors.OnPrimary,
    primaryContainer = KrushiColors.PrimaryContainer,
    onPrimaryContainer = KrushiColors.OnPrimaryContainer,
    secondary = KrushiColors.Secondary,
    onSecondary = KrushiColors.OnSecondary,
    secondaryContainer = KrushiColors.SecondaryContainer,
    onSecondaryContainer = KrushiColors.OnSecondaryContainer,
    background = KrushiColors.Background,
    onBackground = KrushiColors.OnBackground,
    surface = KrushiColors.Surface,
    onSurface = KrushiColors.OnSurface,
    surfaceVariant = KrushiColors.SurfaceVariant,
    onSurfaceVariant = KrushiColors.OnSurfaceVariant,
    outline = KrushiColors.Border,
    outlineVariant = KrushiColors.BorderLight,
    scrim = KrushiColors.Scrim,
    error = KrushiColors.RiskUrgent,
    onError = KrushiColors.TextOnDark
)

private val DarkColorScheme = darkColorScheme(
    primary = KrushiColors.DarkPrimary,
    onPrimary = KrushiColors.TextPrimary,
    primaryContainer = KrushiColors.DarkPrimaryContainer,
    onPrimaryContainer = KrushiColors.PrimaryLight,
    secondary = KrushiColors.SecondaryLight,
    onSecondary = KrushiColors.TextPrimary,
    secondaryContainer = KrushiColors.SecondaryDark,
    onSecondaryContainer = KrushiColors.SecondaryLight,
    background = KrushiColors.DarkBackground,
    onBackground = KrushiColors.DarkOnBackground,
    surface = KrushiColors.DarkSurface,
    onSurface = KrushiColors.DarkOnSurface,
    surfaceVariant = KrushiColors.DarkSurfaceVariant,
    onSurfaceVariant = KrushiColors.DarkOnBackground,
    outline = KrushiColors.DarkSurfaceVariant,
    outlineVariant = KrushiColors.DarkSurfaceVariant,
    scrim = KrushiColors.Scrim,
    error = KrushiColors.RiskUrgent,
    onError = KrushiColors.TextOnDark
)

@Composable
fun KrushiEdgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KrushiTypography,
        content = content
    )
}
