package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient

// ── Allocation Details ──

/**
 * Data class for a single recipient's allocation snapshot on a day.
 */
data class AllocationDetailEntry(
    val start: Int,
    val end: Int,
    val goal: Int
)

/**
 * Builds the allocationDetails JSON string from all recipients and the current day's existing details.
 * On first call for a recipient today, `start` is set from existing details or computed as
 * `burnedCount - todayBurned` (the count at the start of the day). `end` is always the current burnedCount.
 *
 * Format: {"RecipientName": {"start": N, "end": N, "goal": N}, ...}
 *
 * @param recipients All recipients with their current (post-action) state.
 * @param existingDetailsJson Current allocationDetails JSON for today.
 * @param burnDetailsJson Current littleHouseBurnDetails JSON (today's per-recipient counts).
 */
internal fun buildAllocationDetailsJson(
    recipients: List<LittleHouseRecipient>,
    existingDetailsJson: String,
    burnDetailsJson: String = ""
): String {
    val existing = parseAllocationDetails(existingDetailsJson)
    val todayCounts = parseBurnDetailsToMap(burnDetailsJson)
    return buildString {
        append("{")
        append(recipients.joinToString(",") { recipient ->
            val prev = existing[recipient.name]
            // If we already have a start for this recipient, keep it; otherwise compute start-of-day
            val start = prev?.start ?: (recipient.burnedCount - (todayCounts[recipient.name] ?: 0))
            "\"${escapeJsonString(recipient.name)}\":{\"start\":$start,\"end\":${recipient.burnedCount},\"goal\":${recipient.goal}}"
        })
        append("}")
    }
}

/**
 * Parses allocationDetails JSON into a map.
 * Format: {"name": {"start": N, "end": N, "goal": N}, ...}
 */
internal fun parseAllocationDetails(json: String): Map<String, AllocationDetailEntry> {
    if (json.isBlank() || !json.startsWith("{")) return emptyMap()
    val result = mutableMapOf<String, AllocationDetailEntry>()
    try {
        val inner = json.removePrefix("{").removeSuffix("}")
        if (inner.isBlank()) return emptyMap()
        val topEntries = splitJsonTopLevel(inner)
        for (entry in topEntries) {
            val colonIdx = entry.indexOf(':')
            if (colonIdx <= 0) continue
            val key = entry.substring(0, colonIdx).trim().removeSurrounding("\"")
            val valueStr = entry.substring(colonIdx + 1).trim()
            val fields = parseIntFieldsFromObject(valueStr)
            result[key] = AllocationDetailEntry(
                start = fields["start"] ?: 0,
                end = fields["end"] ?: 0,
                goal = fields["goal"] ?: 0
            )
        }
    } catch (_: Exception) { /* malformed */ }
    return result
}

// ── Flat JSON helpers ──

/**
 * Parses a flat JSON like {"Name1":"2","Name2":"1"} into Map<String, Int>.
 */
internal fun parseBurnDetailsToMap(json: String): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    if (json.isBlank() || !json.startsWith("{") || !json.endsWith("}")) return map
    val inner = json.removePrefix("{").removeSuffix("}")
    if (inner.isBlank()) return map
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
    return map
}

// ── Mantra Recited Details ──

/**
 * Tracks a single mantra's count changes for a day, broken down by source.
 */
data class MantraRecitedEntry(
    val recited: Int,    // net additions from user increment / set
    val homework: Int,   // total deducted by homework
    val littleHouse: Int, // total deducted by little house conversion
    val start: Int,      // mantra count at start of day (first interaction)
    val end: Int         // mantra count after most recent interaction
)

/**
 * Reason for a mantra count change, determines which bucket to update.
 */
enum class MantraChangeReason {
    RECITED,      // user increment or positive set
    HOMEWORK,     // homework deduction
    LITTLE_HOUSE  // little house conversion deduction
}

/**
 * Parses mantraRecitedDetails JSON into a map.
 */
internal fun parseMantraRecitedDetails(json: String): Map<String, MantraRecitedEntry> {
    if (json.isBlank() || !json.startsWith("{")) return emptyMap()
    val result = mutableMapOf<String, MantraRecitedEntry>()
    try {
        val inner = json.removePrefix("{").removeSuffix("}")
        if (inner.isBlank()) return emptyMap()
        val topEntries = splitJsonTopLevel(inner)
        for (entry in topEntries) {
            val colonIdx = entry.indexOf(':')
            if (colonIdx <= 0) continue
            val key = entry.substring(0, colonIdx).trim().removeSurrounding("\"")
            val valueStr = entry.substring(colonIdx + 1).trim()
            val fields = parseIntFieldsFromObject(valueStr)
            result[key] = MantraRecitedEntry(
                recited = fields["recited"] ?: 0,
                homework = fields["homework"] ?: 0,
                littleHouse = fields["littleHouse"] ?: 0,
                start = fields["start"] ?: 0,
                end = fields["end"] ?: 0
            )
        }
    } catch (_: Exception) { /* malformed */ }
    return result
}

/**
 * Builds mantraRecitedDetails JSON from a map.
 */
internal fun buildMantraRecitedDetailsJson(entries: Map<String, MantraRecitedEntry>): String {
    if (entries.isEmpty()) return ""
    return buildString {
        append("{")
        append(entries.entries.joinToString(",") { (name, e) ->
            "\"${escapeJsonString(name)}\":{\"recited\":${e.recited},\"homework\":${e.homework},\"littleHouse\":${e.littleHouse},\"start\":${e.start},\"end\":${e.end}}"
        })
        append("}")
    }
}

/**
 * Updates mantra recited details when a mantra count changes.
 *
 * @param existingJson Current mantraRecitedDetails JSON.
 * @param mantraName Name of the mantra.
 * @param oldCount The mantra count before this change.
 * @param newCount The mantra count after this change.
 * @param reason Why the count changed (recited, homework, littleHouse).
 * @return Updated JSON string.
 */
internal fun updateMantraRecitedForCountChange(
    existingJson: String,
    mantraName: String,
    oldCount: Int,
    newCount: Int,
    reason: MantraChangeReason = MantraChangeReason.RECITED
): String {
    val delta = newCount - oldCount
    if (delta == 0) return existingJson

    val entries = parseMantraRecitedDetails(existingJson).toMutableMap()
    val existing = entries[mantraName]

    if (existing != null) {
        entries[mantraName] = when (reason) {
            MantraChangeReason.RECITED -> existing.copy(
                recited = (existing.recited + delta).coerceAtLeast(0),
                end = newCount
            )
            MantraChangeReason.HOMEWORK -> existing.copy(
                homework = (existing.homework + (-delta)).coerceAtLeast(0), // delta is negative, store as positive
                end = newCount
            )
            MantraChangeReason.LITTLE_HOUSE -> existing.copy(
                littleHouse = (existing.littleHouse + (-delta)).coerceAtLeast(0),
                end = newCount
            )
        }
    } else {
        // First interaction today — start is the old count
        entries[mantraName] = when (reason) {
            MantraChangeReason.RECITED -> MantraRecitedEntry(
                recited = delta.coerceAtLeast(0), homework = 0, littleHouse = 0,
                start = oldCount, end = newCount
            )
            MantraChangeReason.HOMEWORK -> MantraRecitedEntry(
                recited = 0, homework = (-delta).coerceAtLeast(0), littleHouse = 0,
                start = oldCount, end = newCount
            )
            MantraChangeReason.LITTLE_HOUSE -> MantraRecitedEntry(
                recited = 0, homework = 0, littleHouse = (-delta).coerceAtLeast(0),
                start = oldCount, end = newCount
            )
        }
    }

    return buildMantraRecitedDetailsJson(entries)
}

// ── Shared JSON utilities ──

/**
 * Splits a JSON object's inner content into top-level entries,
 * respecting nested braces and quoted strings.
 */
internal fun splitJsonTopLevel(inner: String): List<String> {
    val entries = mutableListOf<String>()
    var braceDepth = 0
    var inQuote = false
    var current = StringBuilder()
    for (ch in inner) {
        when {
            ch == '"' && braceDepth == 0 -> { inQuote = !inQuote; current.append(ch) }
            ch == '"' && braceDepth > 0 -> { current.append(ch) }
            ch == '{' && !inQuote -> { braceDepth++; current.append(ch) }
            ch == '}' && !inQuote -> { braceDepth--; current.append(ch) }
            ch == ',' && braceDepth == 0 && !inQuote -> { entries.add(current.toString()); current = StringBuilder() }
            else -> current.append(ch)
        }
    }
    if (current.isNotEmpty()) entries.add(current.toString())
    return entries
}

/**
 * Parses a simple flat JSON object like {"key1":123,"key2":456} into Map<String, Int>.
 */
internal fun parseIntFieldsFromObject(json: String): Map<String, Int> {
    val map = mutableMapOf<String, Int>()
    val inner = json.trim().removePrefix("{").removeSuffix("}")
    if (inner.isBlank()) return map
    val parts = inner.split(",")
    for (part in parts) {
        val colonIdx = part.indexOf(':')
        if (colonIdx <= 0) continue
        val key = part.substring(0, colonIdx).trim().removeSurrounding("\"")
        val value = part.substring(colonIdx + 1).trim()
        val intValue = value.toIntOrNull() ?: 0
        map[key] = intValue
    }
    return map
}

internal fun escapeJsonString(s: String): String =
    s.replace("\\", "\\\\").replace("\"", "\\\"")

