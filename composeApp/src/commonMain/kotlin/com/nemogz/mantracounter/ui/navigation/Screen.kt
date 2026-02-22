package com.nemogz.mantracounter.ui.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Detail(val counterId: String) : Screen()

    @Serializable
    data object Homework : Screen()

    @Serializable
    data object Calendar : Screen()

    @Serializable
    data object LittleHouse : Screen()

    @Serializable
    data object Settings : Screen()
}
