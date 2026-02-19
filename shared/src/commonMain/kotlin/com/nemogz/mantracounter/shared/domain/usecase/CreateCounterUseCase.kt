package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import kotlinx.datetime.Clock
import kotlin.random.Random

class CreateCounterUseCase(
    private val repository: ICounterRepository
) {
    suspend operator fun invoke(name: String, targetWait: Int = 0, initialCount: Int = 0) {
        val newCounter = Counter(
            id = Clock.System.now().toEpochMilliseconds().toString() + Random.nextInt(1000), // Simple ID generation
            mantraType = MantraType.Other, // User created counters count as "Other" (Custom)
            name = name,
            count = initialCount,
            homeworkGoal = 0, // Default 0
            sortOrder = 9999 // Put at end, Repository/ViewModel logic should handle re-sorting if needed, or we rely on the list append
        )
        // We might want to save the specific target/limit if we had a field for it, 
        // but MantraType.Other has 0 mantraGoalCount. 
        // If the user wants a custom target, we'd need to expand the model. 
        // For now, adhering to existing model, "Other" type doesn't enforce a specific target 
        // unless we modify Counter to store a custom target. 
        // The current Counter model relies on MantraType for target.
        // I will stick to the existing model for now.
        
        repository.saveCounter(newCounter)
    }
}
