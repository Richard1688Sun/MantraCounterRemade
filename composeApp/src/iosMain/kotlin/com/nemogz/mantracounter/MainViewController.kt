package com.nemogz.mantracounter

import androidx.compose.ui.window.ComposeUIViewController
import com.nemogz.mantracounter.di.initKoin
import com.nemogz.mantracounter.di.iosModule

fun MainViewController() = ComposeUIViewController { App() }

fun initKoinIos() {
    initKoin {
        modules(iosModule)
    }
}
