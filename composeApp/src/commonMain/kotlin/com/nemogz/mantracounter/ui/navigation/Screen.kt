package com.nemogz.mantracounter.ui.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Detail(val counterId: String) : Screen()
}
