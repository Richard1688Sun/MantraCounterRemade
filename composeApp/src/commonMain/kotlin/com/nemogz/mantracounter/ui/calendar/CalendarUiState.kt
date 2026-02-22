package com.nemogz.mantracounter.ui.calendar

import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val activitiesByDate: Map<LocalDate, DailyActivity> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val isLoading: Boolean = false,
    /**
     * For the selected date: list of DailyActivity rows whose homework was completed on that date.
     * Populated via a DAO query when a day is selected.
     */
    val homeworksCompletedOnSelectedDate: List<DailyActivity> = emptyList()
)
