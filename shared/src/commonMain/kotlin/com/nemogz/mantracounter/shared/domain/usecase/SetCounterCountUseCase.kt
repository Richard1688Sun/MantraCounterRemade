package com.nemogz.mantracounter.shared.domain.usecase


import com.nemogz.mantracounter.shared.domain.model.Counter
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
    suspend operator fun invoke(counter: Counter, amount: Int) {
        val cappedCount = if (amount > 9999999) 9999999 else if (amount < 0) 0 else amount
        val oldCount = counter.count

        if (oldCount == cappedCount) return

        val newCounter = counter.copy(count = cappedCount)
        counterRepository.updateCount(counter.id, cappedCount)

        // Only log positive additions to DailyActivity if it increased
        // Wait, what if the user corrects a mistake downwards?
        // Let's log whatever the net change is so we have accurate start and end counts.
        if (oldCount != cappedCount) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            val activity = dailyActivityRepository.getDailyActivityByDate(today)
            if (activity == null) {
                com.nemogz.mantracounter.shared.util.platformLog("DailyActivity", "ERROR: DailyActivity missing for today in SetCounterCountUseCase")
                return
            }
            val updatedActivity = updateMantraRecitedForCountChange(
                activity, counter, oldCount, cappedCount
            )
            dailyActivityRepository.updateActivity(updatedActivity)
        }
    }
}
