package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey
    val date: Long, // Epoch day
    val homeworkCompleted: Boolean = false,
    val homeworkDetails: String = "", // JSON string mapping Mantra ID -> Completed Count
    val littleHousesConverted: Int = 0,
    val littleHousesBurned: Int = 0
)
