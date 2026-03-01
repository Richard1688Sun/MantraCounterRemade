package com.nemogz.mantracounter.ui.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberPlatformContext(): Any {
    // Desktop doesn't have an equivalent to Android's Context that basic-sound would need,
    // so we just return a dummy object.
    return Any()
}
