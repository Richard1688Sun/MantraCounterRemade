package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository

class GetCounterByIdUseCase(
    private val counterRepository: ICounterRepository
) {
    suspend operator fun invoke(id: String): Counter? {
        return counterRepository.getCounterById(id)
    }
}
