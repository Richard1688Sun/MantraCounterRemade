package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository

class DeleteLittleHouseRecipientUseCase(
    private val repository: ILittleHouseRecipientRepository
) {
    /**
     * Deletes a recipient by ID. Returns false if attempting to delete the default "Self" recipient.
     */
    suspend operator fun invoke(id: String): Boolean {
        if (id == LittleHouseRecipient.DEFAULT_SELF_ID) return false
        repository.deleteById(id)
        return true
    }
}

