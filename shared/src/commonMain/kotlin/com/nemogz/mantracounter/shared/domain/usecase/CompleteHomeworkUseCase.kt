package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CompleteHomeworkUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Checks if homework can be completed and deducts counts if possible.
     * Also logs the deductions in mantraRecitedDetails as homework reason.
     * @return A map of counter name -> deducted amount, or null if not enough counts.
     */
    suspend operator fun invoke(): Map<String, Int>? {
        val counters = counterRepository.getAllCounters().first()
        
        // 1. Filter counters that have homework requirements
        val homeworkCounters = counters.filter { it.homeworkGoal > 0 }
        
        if (homeworkCounters.isEmpty()) return null

        // 2. Check availability
        val allSatisfied = homeworkCounters.all { it.hasEnoughForHomework() }
        
        if (!allSatisfied) return null

        // 3. Deduct counts and build details map
        val idsToUpdate = mutableListOf<String>()
        val newCounts = mutableListOf<Int>()
        val details = mutableMapOf<String, Int>()

        // Capture old counts before deduction
        val oldCounts = mutableMapOf<String, Int>() // name -> old count

        homeworkCounters.forEach { counter ->
            idsToUpdate.add(counter.id)
            val newCount = counter.count - counter.homeworkGoal
            newCounts.add(newCount)
            details[counter.name] = counter.homeworkGoal
            oldCounts[counter.name] = counter.count
        }

        counterRepository.updateCounts(idsToUpdate, newCounts)

        // 4. Log homework deductions in mantraRecitedDetails
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivityEntity(date = today)
        var updatedMantraDetails = activity.mantraRecitedDetails

        homeworkCounters.forEach { counter ->
            val oldCount = oldCounts[counter.name] ?: counter.count
            val newCount = oldCount - counter.homeworkGoal
            updatedMantraDetails = updateMantraRecitedForCountChange(
                updatedMantraDetails, counter.name, oldCount, newCount, MantraChangeReason.HOMEWORK
            )
        }

        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(mantraRecitedDetails = updatedMantraDetails)
        )

        return details
    }
}
