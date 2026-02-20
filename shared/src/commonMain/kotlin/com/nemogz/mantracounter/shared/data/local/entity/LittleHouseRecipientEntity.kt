package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "little_house_recipients")
data class LittleHouseRecipientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val goal: Int = 0,
    val targetFinishDate: Long? = null, // Epoch day, nullable
    val burnedCount: Int = 0,
    val sortOrder: Int = 0
)

