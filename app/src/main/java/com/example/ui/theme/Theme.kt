package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = PureWhite,
    primaryContainer = RedBgLight,
    onPrimaryContainer = RedDark,
    secondary = RedLight,
    onSecondary = PureWhite,
    secondaryContainer = RedBgSoft,
    onSecondaryContainer = RedDark,
    tertiary = Color(0xFF2E7D32),
    onTertiary = PureWhite,
    background = ScreenBg,
    onBackground = TextMain,
    surface = CardSurface,
    onSurface = TextMain,
    surfaceVariant = RedBgSoft,
    onSurfaceVariant = TextSecondary,
    outline = RedBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = RedLight,
    onPrimary = PureWhite,
    primaryContainer = RedDark,
    onPrimaryContainer = RedBgLight,
    secondary = RedAccent,
    onSecondary = PureWhite,
    background = Color(0xFF181213),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF221719),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF332023),
    onSurfaceVariant = Color(0xFFE0D0D2),
    outline = Color(0xFF6B3036)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
