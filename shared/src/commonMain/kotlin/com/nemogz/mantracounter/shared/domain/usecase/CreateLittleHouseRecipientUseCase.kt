package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.model.LittleHouseAllocationDetails
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CreateLittleHouseRecipientUseCase(
    private val repository: ILittleHouseRecipientRepository,
    private val dailyActivityRepository: IDailyActivityRepository
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
        repository.insertLittleHouseRecipient(recipient)
        
        // Add default LittleHouseAllocationDetails to today's activity
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val existingActivity = dailyActivityRepository.getDailyActivityByDate(today)
        if (existingActivity != null) {
            dailyActivityRepository.insertAllocationDetail(
                LittleHouseAllocationDetails(
                    key = LittleHouseAllocationDetails.generateKey(today, recipient.id),
                    dailyActivityDate = today,
                    recipientId = recipient.id,
                    recipientName = recipient.name,
                    recipientSortOrder = recipient.sortOrder,
                    startCount = 0,
                    endCount = 0,
                    allocationGoal = recipient.goal,
                    recipientTargetFinishDate = recipient.targetFinishDate
                )
            )
        }
    }
}


