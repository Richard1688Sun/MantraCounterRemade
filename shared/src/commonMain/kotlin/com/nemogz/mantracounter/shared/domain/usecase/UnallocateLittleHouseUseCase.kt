package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.coroutines.flow.first
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
        recipientRepository.incrementBurnedCount(recipientId, -1)

        // 3. Update daily activity log
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivityEntity(date = today)

        val updatedBurnDetails = decrementBurnDetailsJson(activity.littleHouseBurnDetails, recipient.name)
        val newBurnedCount = (activity.littleHousesBurned - 1).coerceAtLeast(0)

        // Build allocation details from all recipients (post-decrement state)
        val allRecipients = recipientRepository.getAll().first()
        val details = buildAllocationDetailsJson(allRecipients, activity.allocationDetails, updatedBurnDetails)

        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(
                littleHousesBurned = newBurnedCount,
                littleHouseBurnDetails = updatedBurnDetails,
                allocationDetails = details
            )
        )

        return true
    }

    /**
     * Decrements the count for recipientName in the JSON, removing the entry if it reaches 0.
     */
    private fun decrementBurnDetailsJson(existingJson: String, recipientName: String): String {
        val map = mutableMapOf<String, Int>()

        if (existingJson.isNotBlank() && existingJson.startsWith("{") && existingJson.endsWith("}")) {
            val inner = existingJson.removePrefix("{").removeSuffix("}")
            if (inner.isNotBlank()) {
                val entries = mutableListOf<String>()
                var depth = 0
                var current = StringBuilder()
                for (ch in inner) {
                    when {
                        ch == '"' -> { depth = if (depth == 0) 1 else 0; current.append(ch) }
                        ch == ',' && depth == 0 -> { entries.add(current.toString()); current = StringBuilder() }
                        else -> current.append(ch)
                    }
                }
                if (current.isNotEmpty()) entries.add(current.toString())

                for (entry in entries) {
                    val colonIdx = entry.indexOf(':')
                    if (colonIdx > 0) {
                        val key = entry.substring(0, colonIdx).trim().removeSurrounding("\"")
                        val value = entry.substring(colonIdx + 1).trim().removeSurrounding("\"")
                        val intValue = value.toIntOrNull() ?: 0
                        map[key] = intValue
                    }
                }
            }
        }

        val newCount = ((map[recipientName] ?: 0) - 1).coerceAtLeast(0)
        if (newCount > 0) {
            map[recipientName] = newCount
        } else {
            map.remove(recipientName)
        }

        if (map.isEmpty()) return ""

        return buildString {
            append("{")
            append(map.entries.joinToString(",") { (key, value) ->
                "\"$key\":\"$value\""
            })
            append("}")
        }
    }
}
