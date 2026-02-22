package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_activity",
    foreignKeys = [
        ForeignKey(
            entity = DailyActivityEntity::class,
            parentColumns = ["date"],
            childColumns = ["homeworkCompletedDate"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DailyActivityEntity(
    @PrimaryKey
    val date: Long, // Epoch day
    /** FK → the epoch-day row on which this day's homework was actually performed. Null = not yet done. */
    val homeworkCompletedDate: Long? = null,
    val littleHousesConverted: Int = 0
)
