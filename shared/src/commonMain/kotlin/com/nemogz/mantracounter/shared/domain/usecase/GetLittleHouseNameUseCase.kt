package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.Flow

class GetLittleHouseNameUseCase(
    private val repository: ILittleHouseRepository
) {
    operator fun invoke(): Flow<String> {
        return repository.getLittleHouseName()
    }
}
