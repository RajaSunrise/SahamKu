package com.investra.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(val label: String) {
    DARK("Mode Gelap"),
    LIGHT("Mode Terang"),
    SYSTEM("Sistem")
}

data class AppColors(
    val bg: Color,
    val surface: Color,
    val surfaceHigh: Color,
    val surfaceHighest: Color,
    val surfaceLow: Color,
    val surfaceLowest: Color,
    val textMain: Color,
    val textMuted: Color
)

val DarkAppColors = AppColors(
    bg = Color(0xFF10141D),
    surface = Color(0xFF161B22),
    surfaceHigh = Color(0xFF21262D),
    surfaceHighest = Color(0xFF30363D),
    surfaceLow = Color(0xFF13171F),
    surfaceLowest = Color(0xFF090D12),
    textMain = Color(0xFFF0F6FC),
    textMuted = Color(0xFF8B949E)
)

val LightAppColors = AppColors(
    bg = Color(0xFFF1F5F9),
    surface = Color(0xFFFFFFFF),
    surfaceHigh = Color(0xFFE2E8F0),
    surfaceHighest = Color(0xFFCBD5E1),
    surfaceLow = Color(0xFFF8FAFC),
    surfaceLowest = Color(0xFFFFFFFF),
    textMain = Color(0xFF0F172A),
    textMuted = Color(0xFF64748B)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    secondary = SecondaryBlue,
    tertiary = TertiaryContainer,
    background = DarkAppColors.bg,
    surface = DarkAppColors.surface,
    onBackground = DarkAppColors.textMain,
    onSurface = DarkAppColors.textMain
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryEmerald,
    secondary = SecondaryBlue,
    tertiary = TertiaryContainer,
    background = LightAppColors.bg,
    surface = LightAppColors.surface,
    onBackground = LightAppColors.textMain,
    onSurface = LightAppColors.textMain
)

@Composable
fun InvestraTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val appColors = if (isDark) DarkAppColors else LightAppColors
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
