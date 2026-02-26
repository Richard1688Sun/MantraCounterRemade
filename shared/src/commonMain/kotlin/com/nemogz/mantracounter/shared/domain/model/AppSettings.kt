package com.nemogz.mantracounter.shared.domain.model

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class AppSettings(
    val vibrationsEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
