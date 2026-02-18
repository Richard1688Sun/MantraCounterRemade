package com.nemogz.mantracounter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.usecase.CompleteHomeworkUseCase
import com.nemogz.mantracounter.shared.domain.usecase.ConvertLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseCountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.IncrementCounterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCountersUseCase: GetCountersUseCase,
    private val getLittleHouseCountUseCase: GetLittleHouseCountUseCase,
    private val incrementCounterUseCase: IncrementCounterUseCase,
    private val convertLittleHouseUseCase: ConvertLittleHouseUseCase,
    private val completeHomeworkUseCase: CompleteHomeworkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getCountersUseCase(), 
                getLittleHouseCountUseCase()
            ) { counters, littleHouseCount ->
                HomeUiState(
                    counters = counters,
                    littleHouseCount = littleHouseCount,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onIncrementCounter(id: String) {
        viewModelScope.launch {
            incrementCounterUseCase(id)
            convertLittleHouseUseCase() 
        }
    }
    
    fun onCompleteHomework() {
        viewModelScope.launch {
            completeHomeworkUseCase()
        }
    }
}
