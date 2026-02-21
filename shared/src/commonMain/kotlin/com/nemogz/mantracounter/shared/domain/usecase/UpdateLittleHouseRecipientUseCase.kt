package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UpdateLittleHouseRecipientUseCase(
    private val repository: ILittleHouseRecipientRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    suspend operator fun invoke(recipient: LittleHouseRecipient) {
        repository.update(recipient)

        // Rebuild allocationDetails for today so calendar reflects the updated goal
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: return
        val allRecipients = repository.getAll().first()
        val updatedDetails = buildAllocationDetailsJson(
            allRecipients, activity.allocationDetails, activity.littleHouseBurnDetails
        )
        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(allocationDetails = updatedDetails)
        )
    }
}

