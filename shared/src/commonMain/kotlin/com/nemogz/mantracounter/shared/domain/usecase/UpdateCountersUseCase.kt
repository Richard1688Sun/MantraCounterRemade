package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UpdateCountersUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {

    suspend operator fun invoke(counters: List<Counter>) {
        counterRepository.updateCounters(counters)
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: return
        
        val counterMap = counters.associateBy { it.id }
        val updatedMantras = activity.mantras.map { 
            val updatedCounter = counterMap[it.mantraId]
            if (updatedCounter != null) {
                it.copy(
                    mantraName = updatedCounter.name,
                    mantraSortOrder = updatedCounter.sortOrder,
                    homeworkGoal = updatedCounter.homeworkGoal
                )
            } else {
                it
            }
        }
        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(mantras = updatedMantras)
        )
    }
}