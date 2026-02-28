package com.nemogz.mantracounter.shared.domain.model

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

data class AppSettings(
    val vibrationsEnabled: Boolean = true,
    val counterAudioEnabled: Boolean = true,
    val littleHouseAudioEnabled: Boolean = true,
    val homeworkAudioEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
