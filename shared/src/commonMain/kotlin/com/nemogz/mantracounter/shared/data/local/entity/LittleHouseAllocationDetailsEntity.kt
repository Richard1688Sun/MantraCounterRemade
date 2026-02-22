package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(
    tableName = "little_house_allocation_details",
    foreignKeys = [
        ForeignKey(
            entity = DailyActivityEntity::class,
            parentColumns = ["date"],
            childColumns = ["dailyActivityDate"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LittleHouseAllocationDetailsEntity(
    @PrimaryKey
    val key: String,
    val dailyActivityDate: Long, // FK to DailyActivityEntity
    val recipientId: String,
    val recipientName: String,
    val recipientSortOrder: Int,
    val recipientTargetFinishDate: Long?,
    val startCount: Int,
    val endCount: Int,
    val allocationGoal: Int
)
