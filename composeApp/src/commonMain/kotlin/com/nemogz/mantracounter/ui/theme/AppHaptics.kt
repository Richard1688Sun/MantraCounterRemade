package com.nemogz.mantracounter.ui.theme

import androidx.compose.runtime.Composable
import io.github.compose.jindong.JindongScope
import io.github.compose.jindong.core.model.HapticIntensity
import io.github.compose.jindong.core.ms
import io.github.compose.jindong.dsl.*

object AppHaptics {

    @Composable
    fun JindongScope.ShortTap() {
        Haptic(
            duration = 100.ms,
            intensity = HapticIntensity.MEDIUM
        )
    }

    @Composable
    fun JindongScope.LongTap() {
        Haptic(
            duration = 500.ms,
            intensity = HapticIntensity.STRONG
        )
    }
}