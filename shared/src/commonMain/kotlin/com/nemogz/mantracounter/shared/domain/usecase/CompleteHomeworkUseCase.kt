package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import kotlinx.coroutines.flow.first

class CompleteHomeworkUseCase(
    private val counterRepository: ICounterRepository
) {
    /**
     * Checks if homework can be completed and deducts counts if possible.
     * @return true if successful, false if not enough counts.
     */
    suspend operator fun invoke(): Boolean {
        val counters = counterRepository.getAllCounters().first()
        
        // 1. Filter counters that have homework requirements
        val homeworkCounters = counters.filter { it.homeworkGoal > 0 }
        
        if (homeworkCounters.isEmpty()) return false 

        // 2. Check availability
        val allSatisfied = homeworkCounters.all { it.hasEnoughForHomework() }
        
        if (!allSatisfied) return false

        // 3. Deduct counts
        val idsToUpdate = mutableListOf<String>()
        val newCounts = mutableListOf<Int>()

        homeworkCounters.forEach { counter ->
            idsToUpdate.add(counter.id)
            newCounts.add(counter.count - counter.homeworkGoal) // Deduct goal amount
        }

        counterRepository.updateCounts(idsToUpdate, newCounts)
        
        // TODO: Log the homework completion time/history if needed (Repository for HomeworkHistory?)
        
        return true
    }
}
