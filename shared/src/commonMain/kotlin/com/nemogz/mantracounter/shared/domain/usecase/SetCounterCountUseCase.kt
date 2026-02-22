package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.CounterConstants
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class SetCounterCountUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Updates a counter's name and count. The difference (positive or negative)
     * is tracked in mantra details for the day.
     */
    suspend operator fun invoke(counterId: String, newName: String, newCount: Int) {
        val counter = counterRepository.getCounterById(counterId) ?: return
        val cappedCount = newCount.coerceIn(0, CounterConstants.MAX_COUNT)
        val oldCount = counter.count

        // Update the counter
        if (counter.name != newName || counter.count != cappedCount) {
            counterRepository.updateCounter(counter.copy(name = newName, count = cappedCount))
        }

        // Log the delta (positive or negative) as mantra recited
        if (cappedCount != oldCount) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivity(DailyActivityEntity(date = today), emptyList(), emptyList())
            val updatedActivity = updateMantraRecitedForCountChange(
                activity, counter.name, oldCount, cappedCount, counter.homeworkGoal
            )
            dailyActivityRepository.insertOrUpdateActivity(updatedActivity)
        }
    }
}
