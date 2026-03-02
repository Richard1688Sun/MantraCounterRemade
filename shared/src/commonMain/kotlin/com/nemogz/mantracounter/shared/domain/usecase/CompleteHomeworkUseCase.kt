package com.nemogz.mantracounter.shared.domain.usecase

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
     * Deducts homework requirements from counters and marks the specified day as completed today.
     * Logs the deductions in today's mantra details as a homework reason.
     * @param targetDate The epoch day being marked as complete (defaults to today).
     * @return A map of counter name -> deducted amount, or null if not enough counts.
     */
    suspend operator fun invoke(targetDate: Long? = null): Map<String, Int>? {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
        val dateToComplete = targetDate ?: today

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

        // 4. Log homework deductions in TODAY'S mantra details (because the action happened today)
        homeworkCounters.forEach { counter ->
            val oldCount = oldCounts[counter.name] ?: counter.count
            val newCount = oldCount - counter.homeworkGoal
            val key = com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails.generateKey(today, counter.id)
            dailyActivityRepository.updateMantraCount(key, newCount)
        }

        // 5. Mark the target day as completed
        if (dateToComplete == today) {
            dailyActivityRepository.updateHomeworkCompletedDate(today, today)

            // 6. Update today's goals in the DAO to match current state (for consistency)
            val finalTodayActivity = dailyActivityRepository.getDailyActivityByDate(today)
            finalTodayActivity?.mantras?.forEach { mantra ->
                val matchingCounter = counters.find { it.id == mantra.mantraId }
                if (matchingCounter != null) {
                    dailyActivityRepository.updateMantraGoal(mantra.key, matchingCounter.homeworkGoal)
                }
            }
        } else {
            // We're catching up a past day.
            // Deductions for today were already saved above. 
            // Mark the past day as completed.
            dailyActivityRepository.updateHomeworkCompletedDate(dateToComplete, today)

                // Ensure all current counters have a detail record for that past day so the goals are recorded
                val finalTargetActivity = dailyActivityRepository.getDailyActivityByDate(dateToComplete)
                counters.forEach { counter ->
                    val existingMantra = finalTargetActivity?.mantras?.find { it.mantraId == counter.id }
                    val key = com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails.generateKey(dateToComplete, counter.id)

                    if (existingMantra == null) {
                        val newDetail = com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails(
                            key = key,
                            dailyActivityDate = dateToComplete,
                            mantraId = counter.id,
                            mantraName = counter.name,
                            mantraSortOrder = counter.sortOrder,
                            startCount = 0,
                            endCount = 0,
                            homeworkGoal = counter.homeworkGoal
                        )
                        dailyActivityRepository.insertMantraDetail(newDetail)
                    } else {
                        dailyActivityRepository.updateMantraGoal(key, counter.homeworkGoal)
                    }
                }
            }
        return details
    }
}
