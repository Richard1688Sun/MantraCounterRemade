package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository

class IncrementCounterUseCase(
    private val counterRepository: ICounterRepository
) {
    suspend operator fun invoke(counterId: String) {
        val counter = counterRepository.getCounterById(counterId) ?: return
        val newCounter = counter.increment()
        counterRepository.saveCounter(newCounter)
    }
}
