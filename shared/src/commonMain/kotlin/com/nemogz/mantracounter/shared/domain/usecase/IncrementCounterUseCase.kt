package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
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
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivityEntity(date = today)
        val updatedMantraDetails = updateMantraRecitedForCountChange(
            activity.mantraRecitedDetails, counter.name, oldCount, newCounter.count
        )
        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(mantraRecitedDetails = updatedMantraDetails)
        )
    }
}
