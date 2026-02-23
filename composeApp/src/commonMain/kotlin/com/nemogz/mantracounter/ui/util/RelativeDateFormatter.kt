package com.nemogz.mantracounter.ui.util

import androidx.compose.runtime.Composable
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Converts an epoch day to a human-readable relative date string.
 * e.g. "Today", "Yesterday", "3 days ago", "2 weeks ago", "1 month ago"
 */
@Composable
fun formatRelativeDate(epochDay: Long): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val date = LocalDate.fromEpochDays(epochDay.toInt())
    val daysDiff = (today.toEpochDays() - date.toEpochDays()).toLong()

    return when {
        daysDiff <= 0L -> stringResource(Res.string.date_today)
        daysDiff == 1L -> stringResource(Res.string.date_yesterday)
        daysDiff < 7L -> stringResource(Res.string.date_days_ago, daysDiff.toInt())
        daysDiff < 14L -> stringResource(Res.string.date_1_week_ago)
        daysDiff < 30L -> stringResource(Res.string.date_weeks_ago, (daysDiff / 7).toInt())
        daysDiff < 60L -> stringResource(Res.string.date_1_month_ago)
        daysDiff < 365L -> stringResource(Res.string.date_months_ago, (daysDiff / 30).toInt())
        else -> stringResource(Res.string.date_years_ago, (daysDiff / 365).toInt())
    }
}
