package com.nemogz.mantracounter.ui.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Converts an epoch day to a human-readable relative date string.
 * e.g. "Today", "Yesterday", "3 days ago", "2 weeks ago", "1 month ago"
 */
fun formatRelativeDate(epochDay: Long): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val date = LocalDate.fromEpochDays(epochDay.toInt())
    val daysDiff = today.toEpochDays() - date.toEpochDays()

    return when {
        daysDiff <= 0 -> "Today"
        daysDiff == 1 -> "Yesterday"
        daysDiff < 7 -> "$daysDiff days ago"
        daysDiff < 14 -> "1 week ago"
        daysDiff < 30 -> "${daysDiff / 7} weeks ago"
        daysDiff < 60 -> "1 month ago"
        daysDiff < 365 -> "${daysDiff / 30} months ago"
        else -> "${daysDiff / 365} year(s) ago"
    }
}
