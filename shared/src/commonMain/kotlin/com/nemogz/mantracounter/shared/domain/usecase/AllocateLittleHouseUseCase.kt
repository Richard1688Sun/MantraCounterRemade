package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
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
        recipientRepository.incrementBurnedCount(recipientId, 1)

        // 3. Log in daily activity
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivityEntity(date = today)

        // Update burn details JSON
        val updatedBurnDetails = updateBurnDetailsJson(activity.littleHouseBurnDetails, recipient.name)

        // Build allocation details from all recipients (post-increment state)
        val allRecipients = recipientRepository.getAll().first()
        val details = buildAllocationDetailsJson(allRecipients, activity.allocationDetails, updatedBurnDetails)

        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(
                littleHousesBurned = activity.littleHousesBurned + 1,
                littleHouseBurnDetails = updatedBurnDetails,
                allocationDetails = details
            )
        )

        return true
    }

    /**
     * Parses existing JSON like {"Name1":"2","Name2":"1"}, increments the count for recipientName,
     * and returns the updated JSON string.
     */
    private fun updateBurnDetailsJson(existingJson: String, recipientName: String): String {
        val map = mutableMapOf<String, Int>()

        // Parse existing JSON manually
        if (existingJson.isNotBlank() && existingJson.startsWith("{") && existingJson.endsWith("}")) {
            val inner = existingJson.removePrefix("{").removeSuffix("}")
            if (inner.isNotBlank()) {
                // Split by entries: "key":"value" pairs separated by commas
                // Simple parser for flat {"key":"value","key2":"value2"} or {"key":value}
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

        map[recipientName] = (map[recipientName] ?: 0) + 1

        // Build JSON string
        return buildString {
            append("{")
            append(map.entries.joinToString(",") { (key, value) ->
                "\"$key\":\"$value\""
            })
            append("}")
        }
    }
}
