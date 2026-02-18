package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository

class UpdateCountersUseCase(private val counterRepository: ICounterRepository) {

    suspend operator fun invoke(counters: List<Counter>) {
        counterRepository.updateCounters(counters)
    }
}