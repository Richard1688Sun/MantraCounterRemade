package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails

@Entity(
    tableName = "mantra_and_homework_details",
    foreignKeys = [
        ForeignKey(
            entity = DailyActivityEntity::class,
            parentColumns = ["date"],
            childColumns = ["dailyActivityDate"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dailyActivityDate", "mantraId"], unique = true)
    ]
)
data class MantraAndHomeworkDetailsEntity(
    @PrimaryKey
    val key: String,
    val dailyActivityDate: Long, // FK to DailyActivityEntity
    val mantraId: String,
    val mantraName: String,
    val mantraSortOrder: Int,
    val startCount: Int,
    val endCount: Int,
    val homeworkGoal: Int
) {
    companion object {
        fun generateKey(date: Long, mantraId: String): String = 
            MantraAndHomeworkDetails.generateKey(date, mantraId)
    }
}
