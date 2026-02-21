package com.nemogz.mantracounter.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun determineColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    lightScheme: ColorScheme,
    darkScheme: ColorScheme
): ColorScheme {
    // iOS doesn't have dynamic wallpaper colors, so just return the standard themes
    return if (darkTheme) darkScheme else lightScheme
}