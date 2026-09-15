package com.investra.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryEmerald = Color(0xFF10B981)
val PrimaryContainer = Color(0xFF005138)
val SecondaryBlue = Color(0xFF3B82F6)
val TertiaryContainer = Color(0xFFEF4444)

// Dynamic Color Getters reading from LocalAppColors
val BgDark: Color @Composable get() = LocalAppColors.current.bg
val SurfaceContainer: Color @Composable get() = LocalAppColors.current.surface
val SurfaceContainerHigh: Color @Composable get() = LocalAppColors.current.surfaceHigh
val SurfaceContainerHighest: Color @Composable get() = LocalAppColors.current.surfaceHighest
val SurfaceContainerLow: Color @Composable get() = LocalAppColors.current.surfaceLow
val SurfaceContainerLowest: Color @Composable get() = LocalAppColors.current.surfaceLowest
val TextMain: Color @Composable get() = LocalAppColors.current.textMain
val TextMuted: Color @Composable get() = LocalAppColors.current.textMuted
