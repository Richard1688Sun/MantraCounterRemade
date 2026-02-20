package com.nemogz.mantracounter.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Converts an epoch day to a human-readable relative date string.
 * e.g. "Today", "Yesterday", "3 days ago", "2 weeks ago", "1 month ago"
 */
fun formatRelativeDate(epochDay: Long): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val date = LocalDate.fromEpochDays(epochDay.toInt())
    val daysDiff = today.toEpochDays() - date.toEpochDays()

    return when {
        daysDiff <= 0L -> "Today"
        daysDiff == 1L -> "Yesterday"
        daysDiff < 7L -> "$daysDiff days ago"
        daysDiff < 14L -> "1 week ago"
        daysDiff < 30L -> "${daysDiff / 7} weeks ago"
        daysDiff < 60L -> "1 month ago"
        daysDiff < 365L -> "${daysDiff / 30} months ago"
        else -> "${daysDiff / 365} year(s) ago"
    }
}
