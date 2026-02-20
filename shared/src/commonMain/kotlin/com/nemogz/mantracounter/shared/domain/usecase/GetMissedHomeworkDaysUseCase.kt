package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class GetMissedHomeworkDaysUseCase(
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Returns the total number of missed homework days recorded.
     * For now, this just counts activities where homeworkCompleted is false 
     * and the date is before today, or we can check all recorded false days.
     */
    operator fun invoke(): Flow<List<Long>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays().toLong()
        return dailyActivityRepository.getAllActivitiesFlow().map { activities ->
            activities
                .filter { !it.homeworkCompleted && it.date <= today }
                .map { it.date }
        }
    }
}
