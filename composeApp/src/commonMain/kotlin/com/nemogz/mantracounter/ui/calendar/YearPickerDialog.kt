package com.nemogz.mantracounter.ui.calendar

import androidx.compose.runtime.Composable
import kotlinx.datetime.YearMonth

/**
 * Thin wrapper kept for backward-compatibility.
 * Delegates to the shared [com.nemogz.mantracounter.ui.components.YearPickerDialog].
 */
@Composable
internal fun YearPickerDialog(
    currentYear: Int,
    startMonth: YearMonth,
    endMonth: YearMonth,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    com.nemogz.mantracounter.ui.components.YearPickerDialog(
        currentYear = currentYear,
        startMonth = startMonth,
        endMonth = endMonth,
        onYearSelected = onYearSelected,
        onDismiss = onDismiss
    )
}

