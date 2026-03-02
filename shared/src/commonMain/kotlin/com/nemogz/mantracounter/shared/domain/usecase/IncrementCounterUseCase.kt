package com.nemogz.mantracounter.shared.domain.usecase


import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.util.platformLog
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class IncrementCounterUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    suspend operator fun invoke(counter: Counter, amount: Int = 1) {
        val oldCount = counter.count
        val newCounter = counter.copy(count = counter.count + amount)
        counterRepository.updateCount(counter.id, newCounter.count)

        // Log daily activity update
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today)
        if (activity == null) {
            platformLog("DailyActivity", "ERROR: DailyActivity missing for today in IncrementCounterUseCase")
            return
        }
        val updatedActivity = updateMantraRecitedForCountChange(
            activity, counter, oldCount, newCounter.count
        )
        dailyActivityRepository.updateActivity(updatedActivity)
    }
}
