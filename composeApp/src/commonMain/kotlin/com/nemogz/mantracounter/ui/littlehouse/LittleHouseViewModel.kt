package com.nemogz.mantracounter.ui.littlehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.usecase.AllocateLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.CreateLittleHouseRecipientUseCase
import com.nemogz.mantracounter.shared.domain.usecase.DeleteLittleHouseRecipientUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseCountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseRecipientsUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UnallocateLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateLittleHouseRecipientUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LittleHouseViewModel(
    private val getRecipientsUseCase: GetLittleHouseRecipientsUseCase,
    private val getLittleHouseCountUseCase: GetLittleHouseCountUseCase,
    private val allocateLittleHouseUseCase: AllocateLittleHouseUseCase,
    private val unallocateLittleHouseUseCase: UnallocateLittleHouseUseCase,
    private val createRecipientUseCase: CreateLittleHouseRecipientUseCase,
    private val updateRecipientUseCase: UpdateLittleHouseRecipientUseCase,
    private val deleteRecipientUseCase: DeleteLittleHouseRecipientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LittleHouseUiState(isLoading = true))
    val uiState: StateFlow<LittleHouseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getRecipientsUseCase(),
                getLittleHouseCountUseCase()
            ) { recipients, littleHouseCount ->
                LittleHouseUiState(
                    recipients = recipients.sortedBy { it.sortOrder },
                    littleHouseCount = littleHouseCount,
                    isLoading = false,
                    isEditMode = _uiState.value.isEditMode,
                    selectedRecipientIds = _uiState.value.selectedRecipientIds
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onAllocate(recipientId: String) {
        viewModelScope.launch {
            val success = allocateLittleHouseUseCase(recipientId)
            if (!success) {
                _uiState.value = _uiState.value.copy(error = "No little houses available to allocate")
            }
        }
    }

    fun onUnallocate(recipientId: String) {
        viewModelScope.launch {
            val success = unallocateLittleHouseUseCase(recipientId)
            if (!success) {
                _uiState.value = _uiState.value.copy(error = "Cannot unallocate: no burned houses for this recipient")
            }
        }
    }

    fun onCreateRecipient(name: String, goal: Int, targetFinishDate: Long?) {
        viewModelScope.launch {
            createRecipientUseCase(name, goal, targetFinishDate)
        }
    }

    fun onUpdateRecipient(recipient: LittleHouseRecipient) {
        viewModelScope.launch {
            updateRecipientUseCase(recipient)
        }
    }

    fun onDeleteRecipient(id: String) {
        viewModelScope.launch {
            deleteRecipientUseCase(id)
        }
    }

    fun toggleEditMode() {
        val newMode = !_uiState.value.isEditMode
        _uiState.value = _uiState.value.copy(
            isEditMode = newMode,
            selectedRecipientIds = if (!newMode) emptySet() else _uiState.value.selectedRecipientIds
        )
    }

    fun toggleSelection(id: String) {
        if (!_uiState.value.isEditMode) return
        // Don't allow selecting the default "Self" recipient
        if (id == LittleHouseRecipient.DEFAULT_SELF_ID) return

        val currentSelection = _uiState.value.selectedRecipientIds.toMutableSet()
        if (currentSelection.contains(id)) {
            currentSelection.remove(id)
        } else {
            currentSelection.add(id)
        }
        _uiState.value = _uiState.value.copy(selectedRecipientIds = currentSelection)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedRecipientIds = emptySet())
    }

    fun deleteSelectedRecipients() {
        val selectedIds = _uiState.value.selectedRecipientIds.toList()
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            selectedIds.forEach { id ->
                deleteRecipientUseCase(id)
            }
            clearSelection()
        }
    }

    fun onMoveRecipient(fromIndex: Int, toIndex: Int) {
        val currentList = _uiState.value.recipients.toMutableList()
        if (fromIndex in currentList.indices && toIndex in currentList.indices) {
            val item = currentList.removeAt(fromIndex)
            currentList.add(toIndex, item)

            val updatedList = currentList.mapIndexed { index, recipient ->
                recipient.copy(sortOrder = index)
            }
            _uiState.value = _uiState.value.copy(recipients = updatedList)

            viewModelScope.launch {
                updatedList.forEach { updateRecipientUseCase(it) }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

