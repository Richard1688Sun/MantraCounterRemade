package com.nemogz.mantracounter.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.usecase.GetCounterByIdUseCase
import com.nemogz.mantracounter.shared.domain.usecase.IncrementCounterUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateHomeworkAmountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CounterDetailUiState(
    val counter: Counter? = null,
    val isLoading: Boolean = true
)

class CounterDetailViewModel(
    private val getCounterByIdUseCase: GetCounterByIdUseCase,
    private val incrementCounterUseCase: IncrementCounterUseCase,
    private val updateHomeworkAmountUseCase: UpdateHomeworkAmountUseCase,
    private val validateCounterCountUseCase: com.nemogz.mantracounter.shared.domain.usecase.ValidateCounterCountUseCase,
    private val setCounterCountUseCase: com.nemogz.mantracounter.shared.domain.usecase.SetCounterCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CounterDetailUiState())
    val uiState: StateFlow<CounterDetailUiState> = _uiState.asStateFlow()

    fun loadCounter(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            refreshCounter(id)
        }
    }

    private suspend fun refreshCounter(id: String) {
        val counter = getCounterByIdUseCase(id)
        _uiState.value = CounterDetailUiState(counter = counter, isLoading = false)
    }

    fun onIncrement() {
        val currentCounter = uiState.value.counter ?: return
        if (validateCounterCountUseCase(currentCounter.count + 1)) {
            viewModelScope.launch {
                incrementCounterUseCase(currentCounter)
                refreshCounter(currentCounter.id)
            }
        }
    }

    fun onDecrement() {
        val currentCounter = uiState.value.counter ?: return
        if (currentCounter.count > 0) {
            viewModelScope.launch {
                setCounterCountUseCase(
                    counter = currentCounter,
                    amount = currentCounter.count - 1
                )
                refreshCounter(currentCounter.id)
            }
        }
    }
    
    fun onUpdateHomeworkAmount(newAmount: String) {
        val currentId = uiState.value.counter?.id ?: return
        val amount = newAmount.toIntOrNull() ?: return
        viewModelScope.launch {
             updateHomeworkAmountUseCase(currentId, amount)
             refreshCounter(currentId)
        }
    }
}
