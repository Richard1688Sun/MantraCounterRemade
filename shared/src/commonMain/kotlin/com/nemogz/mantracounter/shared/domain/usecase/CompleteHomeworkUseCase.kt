package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
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
     * Also logs the deductions in mantra details as homework reason.
     * @param isCatchUp true if completing homework for a past day. If true, today's DailyActivity is NOT marked as completed.
     * @return A map of counter name -> deducted amount, or null if not enough counts.
     */
    suspend operator fun invoke(isCatchUp: Boolean = false): Map<String, Int>? {
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

        // 4. Log homework deductions in mantra details
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivity(DailyActivityEntity(date = today), emptyList(), emptyList())
        var updatedActivity = activity

        homeworkCounters.forEach { counter ->
            val oldCount = oldCounts[counter.name] ?: counter.count
            val newCount = oldCount - counter.homeworkGoal
            updatedActivity = updateMantraRecitedForCountChange(
                updatedActivity, counter, oldCount, newCount, counter.homeworkGoal
            )
        }

        // Only mark today's activity as completed if we are NOT catching up
        val finalActivity = if (!isCatchUp) {
            updatedActivity.copy(
                activity = updatedActivity.activity.copy(
                    homeworkCompletedDate = today
                )
            )
        } else {
            updatedActivity
        }

        dailyActivityRepository.insertOrUpdateActivity(finalActivity)

        return details
    }
}
