package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CatchUpHomeworkUseCase(
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Marks a homework day as completed.
     * Sets homeworkCompletedDate on the target day's row to point to today's row.
     * @param date The epoch day to mark as complete
     * @param homeworkDetails Ignored, kept for signature backwards compatibility
     */
    suspend operator fun invoke(date: Long, homeworkDetails: String = "") {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()

        // Update the target day's row 
        val activity = dailyActivityRepository.getDailyActivityByDate(date)
            ?: DailyActivity(DailyActivityEntity(date = date), emptyList(), emptyList())

        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(
                activity = activity.activity.copy(homeworkCompletedDate = today)
            )
        )
    }
}
