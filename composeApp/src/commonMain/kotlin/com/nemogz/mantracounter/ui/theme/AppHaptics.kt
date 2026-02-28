package com.nemogz.mantracounter.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.nemogz.mantracounter.shared.util.platformLog
import io.github.compose.jindong.JindongScope
import io.github.compose.jindong.core.model.HapticIntensity
import io.github.compose.jindong.core.ms
import io.github.compose.jindong.dsl.*

object AppHaptics {

    @Composable
    fun JindongScope.ShortTap(isEnabled: Boolean) {
        platformLog("MantraCounterLog", "ShortTap triggered, isEnabled=$isEnabled")
        if (isEnabled) {
            Haptic(
                duration = 100.ms,
                intensity = HapticIntensity.MEDIUM
            )
        }
    }

    @Composable
    fun JindongScope.LongTap(isEnabled: Boolean) {
        platformLog("MantraCounterLog", "LongTap triggered, isEnabled=$isEnabled")
        if (isEnabled) {
            Haptic(
                duration = 500.ms,
                intensity = HapticIntensity.STRONG
            )
        }
    }
}