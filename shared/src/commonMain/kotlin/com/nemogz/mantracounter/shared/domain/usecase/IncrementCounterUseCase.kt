package com.nemogz.mantracounter.shared.domain.usecase


import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails

class IncrementCounterUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    suspend operator fun invoke(counter: Counter, amount: Int = 1) {
        val oldCount = counter.count
        val newCounter = counter.copy(count = counter.count + amount)
        counterRepository.updateCount(counter.id, newCounter.count)

        // Log daily activity update
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
        val key = MantraAndHomeworkDetails.generateKey(today, counter.id)
        dailyActivityRepository.updateMantraCount(key, newCounter.count)
    }
}
