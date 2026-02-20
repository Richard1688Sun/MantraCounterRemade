package com.nemogz.mantracounter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.usecase.CompleteHomeworkUseCase
import com.nemogz.mantracounter.shared.domain.usecase.ConvertLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseCountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.IncrementCounterUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCounterUseCase
import com.nemogz.mantracounter.shared.domain.usecase.ValidateCounterCountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

import com.nemogz.mantracounter.shared.domain.usecase.BurnLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetMissedHomeworkDaysUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val getCountersUseCase: GetCountersUseCase,
    private val getLittleHouseCountUseCase: GetLittleHouseCountUseCase,
    private val incrementCounterUseCase: IncrementCounterUseCase,
    private val convertLittleHouseUseCase: ConvertLittleHouseUseCase,
    private val burnLittleHouseUseCase: BurnLittleHouseUseCase,
    private val getMissedHomeworkDaysUseCase: GetMissedHomeworkDaysUseCase,
    private val completeHomeworkUseCase: CompleteHomeworkUseCase,
    private val catchUpHomeworkUseCase: com.nemogz.mantracounter.shared.domain.usecase.CatchUpHomeworkUseCase,
    private val updateCountersUseCase: UpdateCountersUseCase, // New dependency
    private val updateCounterUseCase: UpdateCounterUseCase,  // New dependency
    private val validateCounterCountUseCase: ValidateCounterCountUseCase, // New dependency
    private val createCounterUseCase: com.nemogz.mantracounter.shared.domain.usecase.CreateCounterUseCase,
    private val deleteCountersUseCase: com.nemogz.mantracounter.shared.domain.usecase.DeleteCountersUseCase,
    private val checkDayRolloverUseCase: com.nemogz.mantracounter.shared.domain.usecase.CheckDayRolloverUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getCountersUseCase(), 
                getLittleHouseCountUseCase(),
                getMissedHomeworkDaysUseCase()
            ) { counters, littleHouseCount, missedDays ->
                HomeUiState(
                    counters = counters.sortedBy { it.sortOrder },
                    littleHouseCount = littleHouseCount,
                    missedHomeworkDays = missedDays,
                    isLoading = false,
                    isEditMode = _uiState.value.isEditMode, 
                    selectedCounterIds = _uiState.value.selectedCounterIds
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun checkDayRollover() {
        viewModelScope.launch {
            checkDayRolloverUseCase()
        }
    }

    fun catchUpDay(epochDay: Long) {
        viewModelScope.launch {
            // First deduct mantra counts — returns map of ID -> deducted amount, or null
            val details = completeHomeworkUseCase()
            if (details != null) {
                // Convert details map to a proper JSON string
                val detailsStr = buildString {
                    append("{")
                    append(details.entries.joinToString(",") { (key, value) ->
                        "\"$key\":\"$value\""
                    })
                    append("}")
                }
                // Then mark the day as completed with the details
                catchUpHomeworkUseCase(epochDay, detailsStr)
            }
        }
    }

    fun toggleEditMode() {
        val newMode = !_uiState.value.isEditMode
        _uiState.value = _uiState.value.copy(
            isEditMode = newMode,
            selectedCounterIds = if (!newMode) emptySet() else _uiState.value.selectedCounterIds
        )
    }

    fun toggleSelection(id: String) {
        if (!_uiState.value.isEditMode) return
        
        val currentSelection = _uiState.value.selectedCounterIds.toMutableSet()
        if (currentSelection.contains(id)) {
            currentSelection.remove(id)
        } else {
            currentSelection.add(id)
        }
        _uiState.value = _uiState.value.copy(selectedCounterIds = currentSelection)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedCounterIds = emptySet())
    }

    fun deleteSelectedCounters() {
        val selectedIds = _uiState.value.selectedCounterIds.toList()
        if (selectedIds.isEmpty()) return
        
        viewModelScope.launch {
            deleteCountersUseCase(selectedIds)
            clearSelection()
        }
    }

    fun onCreateCounter(name: String, targetWait: Int = 0, initialCount: Int = 0) {
        viewModelScope.launch {
            createCounterUseCase(name, targetWait, initialCount)
        }
    }

    fun onIncrementCounter(id: String) {
        val counter = _uiState.value.counters.find { it.id == id } ?: return
        if (validateCounterCountUseCase(counter.count + 1)) {
            viewModelScope.launch {
                incrementCounterUseCase(id)
            }
        }
    }

    fun onConvertLittleHouse() {
        viewModelScope.launch {
            convertLittleHouseUseCase()
        }
    }

    fun onBurnLittleHouse() {
        viewModelScope.launch {
            burnLittleHouseUseCase()
        }
    }
    
    fun onCompleteHomework() {
        viewModelScope.launch {
            val today = kotlin.time.Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date.toEpochDays().toLong()
            val details = completeHomeworkUseCase()
            if (details != null) {
                val detailsStr = buildString {
                    append("{")
                    append(details.entries.joinToString(",") { (key, value) ->
                        "\"$key\":\"$value\""
                    })
                    append("}")
                }
                catchUpHomeworkUseCase(today, detailsStr)
            }
        }
    }

    fun onMoveCounter(fromIndex: Int, toIndex: Int) {
        val currentList = _uiState.value.counters.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)
            
            val updatedList = currentList.mapIndexed { index, counter ->
                counter.copy(sortOrder = index)
            }
            _uiState.value = _uiState.value.copy(counters = updatedList)

            viewModelScope.launch {
                updateCountersUseCase(updatedList)
            }
        }
    }

    fun onUpdateCounter(id: String, newName: String, newCount: Int) {
        val counter = _uiState.value.counters.find { it.id == id } ?: return
        // No need to coerce if we validate, but coercion is safer for "update" vs "increment".
        // Let's coerce to MAX_COUNT instead of hardcoded number, or just check validity.
        // For direct edit, coerceAtMost(MAX) is friendlier than rejecting.
        val cappedCount = newCount.coerceAtMost(com.nemogz.mantracounter.shared.domain.model.CounterConstants.MAX_COUNT)
        
        if (counter.name != newName || counter.count != cappedCount) {
             viewModelScope.launch {
                 updateCounterUseCase(counter.copy(name = newName, count = cappedCount))
             }
        }
    }
}
