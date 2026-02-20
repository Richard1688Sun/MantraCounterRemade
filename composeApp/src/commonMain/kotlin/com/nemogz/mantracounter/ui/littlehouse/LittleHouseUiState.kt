package com.nemogz.mantracounter.ui.littlehouse

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient

data class LittleHouseUiState(
    val recipients: List<LittleHouseRecipient> = emptyList(),
    val littleHouseCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

