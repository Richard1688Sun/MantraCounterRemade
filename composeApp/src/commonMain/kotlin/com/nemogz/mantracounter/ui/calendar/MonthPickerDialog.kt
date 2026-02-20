package com.nemogz.mantracounter.ui.calendar

import androidx.compose.runtime.Composable
import kotlinx.datetime.YearMonth

/**
 * Thin wrapper kept for backward-compatibility.
 * Delegates to the shared [com.nemogz.mantracounter.ui.components.MonthPickerDialog].
 */
@Composable
internal fun MonthPickerDialog(
    currentMonthOrdinal: Int,
    visibleYear: Int,
    startMonth: YearMonth,
    endMonth: YearMonth,
    onMonthSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    com.nemogz.mantracounter.ui.components.MonthPickerDialog(
        currentMonthOrdinal = currentMonthOrdinal,
        visibleYear = visibleYear,
        startMonth = startMonth,
        endMonth = endMonth,
        onMonthSelected = onMonthSelected,
        onDismiss = onDismiss
    )
}

