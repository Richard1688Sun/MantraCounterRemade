package com.nemogz.mantracounter.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import io.github.compose.jindong.JindongScope
import io.github.compose.jindong.core.model.HapticIntensity
import io.github.compose.jindong.core.ms
import io.github.compose.jindong.dsl.*

val LocalVibrationsEnabled = compositionLocalOf { true }

object AppHaptics {

    @Composable
    fun JindongScope.ShortTap() {
        if (LocalVibrationsEnabled.current) {
            Haptic(
                duration = 100.ms,
                intensity = HapticIntensity.MEDIUM
            )
        }
    }

    @Composable
    fun JindongScope.LongTap() {
        if (LocalVibrationsEnabled.current) {
            Haptic(
                duration = 500.ms,
                intensity = HapticIntensity.STRONG
            )
        }
    }
}