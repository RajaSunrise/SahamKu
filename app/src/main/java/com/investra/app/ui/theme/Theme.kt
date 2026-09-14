package com.investra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    secondary = SecondaryBlue,
    tertiary = TertiaryContainer,
    background = BgDark,
    surface = SurfaceContainer
)

@Composable
fun InvestraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
