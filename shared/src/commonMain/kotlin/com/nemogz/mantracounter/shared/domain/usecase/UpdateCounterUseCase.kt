package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository

class UpdateCounterUseCase(private val counterRepository: ICounterRepository) {

    suspend operator fun invoke(counter: Counter) {
        counterRepository.updateCounter(counter)
    }
}