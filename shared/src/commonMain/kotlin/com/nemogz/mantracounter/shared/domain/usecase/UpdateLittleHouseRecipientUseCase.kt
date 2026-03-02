package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseAllocationDetails
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UpdateLittleHouseRecipientUseCase(
    private val repository: ILittleHouseRecipientRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    suspend operator fun invoke(recipient: LittleHouseRecipient) {
        repository.updateDetails(
            id = recipient.id,
            name = recipient.name,
            goal = recipient.goal,
            sortOrder = recipient.sortOrder,
            targetFinishDate = recipient.targetFinishDate
        )

        // Rebuild allocation details for today so calendar reflects the updated goal
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val key = LittleHouseAllocationDetails.generateKey(today, recipient.id)
        
        dailyActivityRepository.updateAllocationDetails(
            key = key,
            sortOrder = recipient.sortOrder,
            targetFinishDate = recipient.targetFinishDate,
            name = recipient.name
        )
        
        dailyActivityRepository.updateAllocationGoal(key, recipient.goal)
    }
}

