package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlin.time.Clock

class CreateLittleHouseRecipientUseCase(
    private val repository: ILittleHouseRecipientRepository
) {
    suspend operator fun invoke(name: String, goal: Int = 0, targetFinishDate: Long? = null) {
        val currentCount = repository.getCount()
        val id = "recipient_${Clock.System.now().toEpochMilliseconds()}"
        val recipient = LittleHouseRecipient(
            id = id,
            name = name,
            goal = goal,
            targetFinishDate = targetFinishDate,
            burnedCount = 0,
            sortOrder = currentCount
        )
        repository.insert(recipient)
    }
}


