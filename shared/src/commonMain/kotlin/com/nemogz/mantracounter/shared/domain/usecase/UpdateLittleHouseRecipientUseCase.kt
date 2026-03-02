package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import com.nemogz.mantracounter.shared.util.platformLog
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
        val activity = dailyActivityRepository.getDailyActivityByDate(today)
        if (activity == null) {
            platformLog("DailyActivity", "ERROR: DailyActivity missing for today in UpdateLittleHouseRecipientUseCase")
            return
        }
        
        val updatedAllocations = activity.allocations.map { 
            if (it.recipientId == recipient.id) {
                it.copy(
                    recipientName = recipient.name,
                    recipientSortOrder = recipient.sortOrder,
                    recipientTargetFinishDate = recipient.targetFinishDate,
                    allocationGoal = recipient.goal
                )
            } else {
                it
            }
        }
        dailyActivityRepository.updateActivity(
            activity.copy(allocations = updatedAllocations)
        )
    }
}

