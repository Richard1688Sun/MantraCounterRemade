package com.nemogz.mantracounter.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.usecase.GetActivitiesForMonthUseCase
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
    private val dailyActivityRepository: IDailyActivityRepository,
    private val getLittleHouseNameUseCase: com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState(isLoading = true))
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    // Track which months have been loaded to avoid redundant fetches
    private val loadedMonths = mutableSetOf<Pair<Int, Int>>()

    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        loadMonth(today.year, today.month.ordinal + 1, forceReload = true)
        
        viewModelScope.launch {
            getLittleHouseNameUseCase().collect { name ->
                _uiState.value = _uiState.value.copy(littleHouseName = name)
            }
        }
    }

    fun onMonthChanged(year: Int, month: Int) {
        loadMonth(year, month)
    }

    fun onDaySelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        // Load which days had their homework completed on this date via SQL query
        viewModelScope.launch {
            val epochDay = date.toEpochDays().toLong()
            val completedHere = dailyActivityRepository.getActivitiesCompletedOnDate(epochDay)
            _uiState.value = _uiState.value.copy(homeworksCompletedOnSelectedDate = completedHere)
        }
    }

    /**
     * Reloads all cached months. Call when navigating back so changes are reflected.
     */
    fun refresh() {
        val monthsToReload = loadedMonths.toList()
        loadedMonths.clear()
        _uiState.value = _uiState.value.copy(activitiesByDate = emptyMap())
        for ((year, month) in monthsToReload) {
            loadMonth(year, month)
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
