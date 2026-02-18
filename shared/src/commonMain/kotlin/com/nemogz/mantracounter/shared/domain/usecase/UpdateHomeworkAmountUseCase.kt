package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository

class UpdateHomeworkAmountUseCase(
    private val counterRepository: ICounterRepository
) {
    suspend operator fun invoke(counterId: String, newAmount: Int) {
        val counter = counterRepository.getCounterById(counterId) ?: return
        val updatedCounter = counter.copy(homeworkAmount = newAmount)
        counterRepository.saveCounter(updatedCounter)
    }
}
