package com.example.stylecraft.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = WarmWhite,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = TerracottaDark,

    secondary = Sage,
    onSecondary = WarmWhite,
    secondaryContainer = Sage.copy(alpha = 0.2f),
    onSecondaryContainer = Sage,

    tertiary = Navy,
    onTertiary = WarmWhite,
    tertiaryContainer = Navy.copy(alpha = 0.1f),
    onTertiaryContainer = Navy,

    background = Cream,
    onBackground = InkBlack,

    surface = WarmWhite,
    onSurface = InkBlack,
    surfaceVariant = Parchment,
    onSurfaceVariant = InkGrey,

    outline = InkLight,
    outlineVariant = Parchment,

    error = Error,
    onError = WarmWhite,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaLight,
    onPrimary = CharcoalDeep,
    primaryContainer = TerracottaDark,
    onPrimaryContainer = TerracottaLight,

    secondary = Sage,
    onSecondary = CharcoalDeep,
    secondaryContainer = Sage.copy(alpha = 0.2f),
    onSecondaryContainer = Sage,

    tertiary = Info,
    onTertiary = CharcoalDeep,
    tertiaryContainer = Info.copy(alpha = 0.2f),
    onTertiaryContainer = Info,

    background = CharcoalDeep,
    onBackground = IvoryText,

    surface = CharcoalMid,
    onSurface = IvoryText,
    surfaceVariant = CharcoalLight,
    onSurfaceVariant = IvoryDim,

    outline = IvoryDim,
    outlineVariant = CharcoalLight,

    error = Error,
    onError = CharcoalDeep,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error
)

@Composable
fun StyleCraftTheme(
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
        typography = Typography,
        content = content
    )
}
