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
    val homeworkDetails: String = "", // JSON string mapping Mantra Name -> Deducted Count
    val littleHousesConverted: Int = 0,
    val littleHousesBurned: Int = 0,
    val littleHouseBurnDetails: String = "", // JSON: {"recipientName": count, ...}
    val mantraRecitedDetails: String = "", // JSON: {"mantraName": {"recited": N, "homework": N, "littleHouse": N, "start": N, "end": N}, ...}
    val allocationDetails: String = "" // JSON: {"recipientName": {"start": N, "end": N, "goal": N}, ...}
)
