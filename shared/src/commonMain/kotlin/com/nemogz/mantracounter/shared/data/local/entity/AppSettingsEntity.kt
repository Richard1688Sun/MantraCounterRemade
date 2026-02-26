package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val vibrationsEnabled: Boolean = true,
    val themeMode: String = "SYSTEM" // Can be "SYSTEM", "LIGHT", "DARK"
)
