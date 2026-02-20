package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository

class CatchUpHomeworkUseCase(
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Marks a missed homework day as completed with details of what was deducted.
     * @param date The epoch day to mark as complete
     * @param homeworkDetails JSON string of counter ID -> deducted amount
     */
    suspend operator fun invoke(date: Long, homeworkDetails: String = "") {
        val activity = dailyActivityRepository.getDailyActivityByDate(date)
        if (activity != null && !activity.homeworkCompleted) {
            dailyActivityRepository.insertOrUpdateActivity(
                activity.copy(
                    homeworkCompleted = true,
                    homeworkDetails = homeworkDetails
                )
            )
        }
    }
}
