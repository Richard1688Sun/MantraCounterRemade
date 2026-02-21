package com.nemogz.mantracounter.ui.littlehouse

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient

data class LittleHouseUiState(
    val recipients: List<LittleHouseRecipient> = emptyList(),
    val littleHouseCount: Int = 0,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val selectedRecipientIds: Set<String> = emptySet(),
    val error: String? = null
)

