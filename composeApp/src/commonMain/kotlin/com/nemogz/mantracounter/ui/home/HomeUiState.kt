package com.nemogz.mantracounter.ui.home

import com.nemogz.mantracounter.shared.domain.model.Counter

data class HomeUiState(
    val counters: List<Counter> = emptyList(),
    val littleHouseCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
