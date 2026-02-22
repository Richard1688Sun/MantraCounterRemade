package com.nemogz.mantracounter.shared.domain.usecase

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
        repository.update(recipient)

        // Rebuild allocation details for today so calendar reflects the updated goal
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: return
        
        val updatedAllocations = activity.allocations.map { 
            if (it.recipientName == recipient.name) {
                it.copy(allocationGoal = recipient.goal)
            } else {
                it
            }
        }
        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(allocations = updatedAllocations)
        )
    }
}

