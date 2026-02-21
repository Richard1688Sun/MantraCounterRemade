package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CatchUpHomeworkUseCase(
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Marks a homework day as completed with details of what was deducted.
     * Sets homeworkCompletedDate on the target day's row to point to today's row.
     * @param date The epoch day to mark as complete
     * @param homeworkDetails JSON string of counter name -> deducted amount
     */
    suspend operator fun invoke(date: Long, homeworkDetails: String = "") {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()

        // Update the target day's row — set FK to today and merge details
        val activity = dailyActivityRepository.getDailyActivityByDate(date)
            ?: DailyActivityEntity(date = date)

        val mergedDetails = mergeHomeworkDetails(activity.homeworkDetails, homeworkDetails)

        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(
                homeworkCompletedDate = today,
                homeworkDetails = mergedDetails
            )
        )
    }

    /**
     * Merges two homework detail JSON strings by summing counts per key.
     */
    private fun mergeHomeworkDetails(existingJson: String, newJson: String): String {
        val map = mutableMapOf<String, Int>()

        parseSimpleJson(existingJson).forEach { (k, v) -> map[k] = (map[k] ?: 0) + v }
        parseSimpleJson(newJson).forEach { (k, v) -> map[k] = (map[k] ?: 0) + v }

        if (map.isEmpty()) return newJson.ifBlank { existingJson }

        return buildString {
            append("{")
            append(map.entries.joinToString(",") { (key, value) ->
                "\"${key}\":\"$value\""
            })
            append("}")
        }
    }

    private fun parseSimpleJson(json: String): Map<String, Int> {
        if (json.isBlank() || !json.startsWith("{")) return emptyMap()
        val map = mutableMapOf<String, Int>()
        val inner = json.removePrefix("{").removeSuffix("}")
        if (inner.isBlank()) return emptyMap()
        val entries = mutableListOf<String>()
        var inQuote = false
        var current = StringBuilder()
        for (ch in inner) {
            when {
                ch == '"' -> { inQuote = !inQuote; current.append(ch) }
                ch == ',' && !inQuote -> { entries.add(current.toString()); current = StringBuilder() }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) entries.add(current.toString())
        for (entry in entries) {
            val colonIdx = entry.indexOf(':')
            if (colonIdx > 0) {
                val key = entry.substring(0, colonIdx).trim().removeSurrounding("\"")
                val value = entry.substring(colonIdx + 1).trim().removeSurrounding("\"")
                map[key] = value.toIntOrNull() ?: 0
            }
        }
        return map
    }
}
