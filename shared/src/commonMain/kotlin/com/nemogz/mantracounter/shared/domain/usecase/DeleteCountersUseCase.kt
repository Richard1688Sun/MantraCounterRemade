package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository

class DeleteCountersUseCase(
    private val repository: ICounterRepository
) {
    suspend operator fun invoke(ids: List<String>) {
        if (ids.isEmpty()) return

        // We should verify that we are only deleting allowed types, 
        // but the UI should enforce this too. 
        // Let's double check by fetching? 
        // Or assume the ID list passed is valid. 
        // For safety, let's fetch to check types if we want to be 100% sure we don't delete Little House items.
        // However, repo.deleteCounter just deletes.
        
        // Optimisation: Just delete. The UI guards are likely sufficient for this app scale, 
        // but domain logic *should* enforce rules.
        
        ids.forEach { id ->
            val counter = repository.getCounterById(id)
            if (counter != null && !counter.mantraType.isLittleHouseComponent) {
                repository.deleteCounter(id)
            }
        }
    }
}
