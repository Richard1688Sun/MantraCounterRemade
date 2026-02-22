package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class IncrementCounterUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    suspend operator fun invoke(counterId: String) {
        val counter = counterRepository.getCounterById(counterId) ?: return
        val oldCount = counter.count
        val newCounter = counter.increment()
        counterRepository.saveCounter(newCounter)

        // Log mantra recited in daily activity
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivity(DailyActivityEntity(date = today), emptyList(), emptyList())
        val updatedActivity = updateMantraRecitedForCountChange(
            activity, counter.name, oldCount, newCounter.count, counter.homeworkGoal
        )
        dailyActivityRepository.insertOrUpdateActivity(updatedActivity)
    }
}
