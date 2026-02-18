package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.Flow

class GetLittleHouseCountUseCase(
    private val littleHouseRepository: ILittleHouseRepository
) {
    operator fun invoke(): Flow<Int> {
        return littleHouseRepository.getLittleHouseCount()
    }
}
