package com.grozzbear.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Grozz is a dark brand app. We always use this scheme so Material components
 * (and future screen migrations) match Login/Home instead of default purple.
 */
private val GrozzColorScheme = darkColorScheme(
    primary = GrozzYellow,
    onPrimary = GrozzOnPrimary,
    secondary = GrozzMuted,
    onSecondary = GrozzOnBackground,
    tertiary = GrozzYellow,
    onTertiary = GrozzOnPrimary,
    background = GrozzBackground,
    onBackground = GrozzOnBackground,
    surface = GrozzSurface,
    onSurface = GrozzOnBackground,
    surfaceVariant = GrozzSystemBar,
    onSurfaceVariant = GrozzTextSecondary,
    outline = GrozzBorder,
    error = GrozzError,
    onError = GrozzOnBackground
)

@Composable
fun ProjectFitnessTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GrozzSystemBar.toArgb()
            window.navigationBarColor = GrozzSystemBar.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = GrozzColorScheme,
        typography = GrozzTypography,
        content = content
    )
}
