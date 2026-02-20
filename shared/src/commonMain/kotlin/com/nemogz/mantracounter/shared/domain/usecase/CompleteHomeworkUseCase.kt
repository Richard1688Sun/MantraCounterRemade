package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import kotlinx.coroutines.flow.first

class CompleteHomeworkUseCase(
    private val counterRepository: ICounterRepository
) {
    /**
     * Checks if homework can be completed and deducts counts if possible.
     * @return A map of counter ID -> deducted amount, or null if not enough counts.
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

        homeworkCounters.forEach { counter ->
            idsToUpdate.add(counter.id)
            newCounts.add(counter.count - counter.homeworkGoal)
            details[counter.name] = counter.homeworkGoal
        }

        counterRepository.updateCounts(idsToUpdate, newCounts)
        
        return details
    }
}
