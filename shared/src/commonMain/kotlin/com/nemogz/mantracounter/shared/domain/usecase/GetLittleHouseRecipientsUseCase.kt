package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.coroutines.flow.Flow

class GetLittleHouseRecipientsUseCase(
    private val repository: ILittleHouseRecipientRepository
) {
    operator fun invoke(): Flow<List<LittleHouseRecipient>> {
        return repository.getAll()
    }
}

