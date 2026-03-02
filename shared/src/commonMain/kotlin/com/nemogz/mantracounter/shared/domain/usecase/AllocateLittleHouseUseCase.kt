package com.nemogz.mantracounter.shared.domain.usecase


import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class AllocateLittleHouseUseCase(
    private val littleHouseRepository: ILittleHouseRepository,
    private val recipientRepository: ILittleHouseRecipientRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Allocates (burns) 1 little house to the given recipient.
     * @param recipientId The ID of the recipient to allocate to.
     * @return true if successfully allocated, false if not enough little houses or recipient not found.
     */
    suspend operator fun invoke(recipientId: String): Boolean {
        val currentCount = littleHouseRepository.getLittleHouseCount().first()
        if (currentCount <= 0) return false

        val recipient = recipientRepository.getById(recipientId) ?: return false

        // 1. Decrement global little house count
        littleHouseRepository.incrementLittleHouseCount(-1)

        // 2. Increment recipient's burned count
        val newBurnedCount = recipient.burnedCount + 1
        recipientRepository.incrementBurnedCount(recipientId, 1)

        // 3. Log in daily activity
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today)
        if (activity == null) {
            com.nemogz.mantracounter.shared.util.platformLog("DailyActivity", "ERROR: DailyActivity missing for today in AllocateLittleHouseUseCase")
            return true
        }

        // Add or update allocation using the new logic
        val updatedActivity = updateAllocationForRecipient(activity, recipient, newBurnedCount)

        dailyActivityRepository.updateActivity(updatedActivity)

        return true
    }
}
