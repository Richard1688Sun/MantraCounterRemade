package com.nemogz.mantracounter.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberPlatformContext(): Any {
    return LocalContext.current
}
