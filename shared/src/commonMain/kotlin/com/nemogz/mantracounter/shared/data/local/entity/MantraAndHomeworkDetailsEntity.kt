package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "mantra_and_homework_details",
    foreignKeys = [
        ForeignKey(
            entity = DailyActivityEntity::class,
            parentColumns = ["date"],
            childColumns = ["dailyActivityDate"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MantraAndHomeworkDetailsEntity(
    @PrimaryKey
    val key: String,
    val dailyActivityDate: Long, // FK to DailyActivityEntity
    val mantraName: String,
    val startCount: Int,
    val endCount: Int,
    val homeworkGoal: Int
)
