package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository

class UpdateLittleHouseRecipientUseCase(
    private val repository: ILittleHouseRecipientRepository
) {
    suspend operator fun invoke(recipient: LittleHouseRecipient) {
        repository.update(recipient)
    }
}

