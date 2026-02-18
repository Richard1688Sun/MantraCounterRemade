package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "little_house")
data class LittleHouseEntity(
    @PrimaryKey val id: Int = 1, // Singleton row
    val count: Int
)
