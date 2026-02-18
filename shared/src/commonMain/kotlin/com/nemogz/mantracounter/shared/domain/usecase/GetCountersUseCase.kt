package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import kotlinx.coroutines.flow.Flow

class GetCountersUseCase(
    private val counterRepository: ICounterRepository
) {
    operator fun invoke(): Flow<List<Counter>> {
        return counterRepository.getAllCounters()
    }
}
