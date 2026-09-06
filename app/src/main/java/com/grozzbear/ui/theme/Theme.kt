package com.grozzbear.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

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
    MaterialTheme(
        colorScheme = GrozzColorScheme,
        typography = GrozzTypography,
        content = content
    )
}
