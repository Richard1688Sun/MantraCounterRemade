package com.nemogz.mantracounter.shared.domain.usecase


import com.nemogz.mantracounter.shared.domain.model.LittleHouseAllocationDetails
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UnallocateLittleHouseUseCase(
    private val littleHouseRepository: ILittleHouseRepository,
    private val recipientRepository: ILittleHouseRecipientRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Unallocates (reverses) 1 little house from the given recipient.
     * Only succeeds if the recipient has at least 1 burned count.
     * @param recipientId The ID of the recipient to unallocate from.
     * @return true if successfully unallocated, false if recipient not found or has 0 burned.
     */
    suspend operator fun invoke(recipientId: String): Boolean {
        val recipient = recipientRepository.getById(recipientId) ?: return false
        if (recipient.burnedCount <= 0) return false

        // 1. Increment global little house count (give it back)
        littleHouseRepository.incrementLittleHouseCount(1)

        // 2. Decrement recipient's burned count
        val newBurnedCount = recipient.burnedCount - 1
        recipientRepository.incrementBurnedCount(recipientId, -1)

        // 3. Update daily activity log
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays()
        val key = LittleHouseAllocationDetails.generateKey(today, recipientId)

        dailyActivityRepository.updateAllocationCount(key, newBurnedCount)

        return true
    }
}
