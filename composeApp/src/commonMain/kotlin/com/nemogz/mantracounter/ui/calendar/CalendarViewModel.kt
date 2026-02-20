package com.nemogz.mantracounter.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.usecase.GetActivitiesForMonthUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseRecipientsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CalendarViewModel(
    private val getActivitiesForMonthUseCase: GetActivitiesForMonthUseCase,
    private val getRecipientsUseCase: GetLittleHouseRecipientsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState(isLoading = true))
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    // Track which months have been loaded to avoid redundant fetches
    private val loadedMonths = mutableSetOf<Pair<Int, Int>>()

    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        loadMonth(today.year, today.month.ordinal + 1, forceReload = true)
        loadRecipients()
    }

    fun onMonthChanged(year: Int, month: Int) {
        loadMonth(year, month)
    }

    fun onDaySelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    /**
     * Reloads all cached months and recipients. Call this when navigating back to the calendar
     * so that any changes (e.g. allocated/unallocated little houses) are reflected.
     */
    fun refresh() {
        val monthsToReload = loadedMonths.toList()
        loadedMonths.clear()
        // Clear existing data so stale entries are removed
        _uiState.value = _uiState.value.copy(activitiesByDate = emptyMap())
        for ((year, month) in monthsToReload) {
            loadMonth(year, month)
        }
        loadRecipients()
    }

    private fun loadRecipients() {
        viewModelScope.launch {
            getRecipientsUseCase().collect { recipients ->
                _uiState.value = _uiState.value.copy(recipients = recipients)
            }
        }
    }

    private fun loadMonth(year: Int, month: Int, forceReload: Boolean = false) {
        val key = year to month
        if (!forceReload && loadedMonths.contains(key)) return
        loadedMonths.add(key)

        viewModelScope.launch {
            val activities = getActivitiesForMonthUseCase(year, month)
            val merged = _uiState.value.activitiesByDate + activities
            _uiState.value = _uiState.value.copy(
                activitiesByDate = merged,
                isLoading = false
            )
        }
    }
}
