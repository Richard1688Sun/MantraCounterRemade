package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UpdateCounterUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {

    suspend operator fun invoke(counter: Counter) {
        counterRepository.updateCounter(counter)
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
        val key = MantraAndHomeworkDetails.generateKey(today, counter.id)
        
        dailyActivityRepository.updateMantraDetails(
            key = key,
            sortOrder = counter.sortOrder,
            name = counter.name
        )
        dailyActivityRepository.updateMantraGoal(key, counter.homeworkGoal)
    }
}