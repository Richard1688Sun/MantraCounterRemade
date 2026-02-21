package com.nemogz.mantracounter.ui.calendar

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val activitiesByDate: Map<LocalDate, DailyActivityEntity> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val isLoading: Boolean = false,
    /**
     * For the selected date: list of DailyActivityEntity rows whose homework was completed on that date.
     * Populated via a DAO query when a day is selected.
     */
    val homeworksCompletedOnSelectedDate: List<DailyActivityEntity> = emptyList()
)
