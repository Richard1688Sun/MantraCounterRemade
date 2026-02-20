package com.nemogz.mantracounter.ui.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.usecase.CatchUpHomeworkUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetMissedHomeworkDaysUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCounterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeworkUiState(
    val counters: List<Counter> = emptyList(),
    val missedDays: List<Long> = emptyList(),
    val isLoading: Boolean = false
)

class HomeworkViewModel(
    private val getCountersUseCase: GetCountersUseCase,
    private val updateCounterUseCase: UpdateCounterUseCase,
    private val getMissedHomeworkDaysUseCase: GetMissedHomeworkDaysUseCase,
    private val catchUpHomeworkUseCase: CatchUpHomeworkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeworkUiState(isLoading = true))
    val uiState: StateFlow<HomeworkUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getCountersUseCase(),
                getMissedHomeworkDaysUseCase()
            ) { counters, missedDays ->
                HomeworkUiState(
                    counters = counters.sortedBy { it.sortOrder },
                    missedDays = missedDays.sorted(),
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun updateHomeworkGoal(counter: Counter, newGoal: Int) {
        val targetGoal = newGoal.coerceAtLeast(0)
        if (targetGoal != counter.homeworkGoal) {
            viewModelScope.launch {
                updateCounterUseCase(counter.copy(homeworkGoal = targetGoal))
            }
        }
    }

    fun catchUpDay(date: Long) {
        viewModelScope.launch {
            catchUpHomeworkUseCase(date)
        }
    }
}
