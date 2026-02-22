package com.nemogz.mantracounter.ui.home

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.MantraType

data class HomeUiState(
    val counters: List<Counter> = emptyList(),
    val littleHouseCount: Int = 0,
    val missedHomeworkDays: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val selectedCounterIds: Set<String> = emptySet(),
    val error: String? = null
) {
    /** True if all counters with homework goals have enough counts to deduct */
    val canCompleteHomework: Boolean
        get() {
            val homeworkCounters = counters.filter { it.homeworkGoal > 0 }
            return homeworkCounters.isNotEmpty() && homeworkCounters.all { it.count >= it.homeworkGoal }
        }

    /** True if all 4 core mantras have enough counts to convert 1 little house */
    val canConvertLittleHouse: Boolean
        get() {
            val coreTypes = listOf(MantraType.DaBei, MantraType.BoRuo, MantraType.WangShen, MantraType.QiFo)
            return coreTypes.all { type ->
                val counter = counters.find { it.mantraType == type }
                counter != null && counter.count >= type.mantraGoalCount
            }
        }

    /** Number of Little Houses that can be converted from current counts */
    val convertibleLittleHouseCount: Int
        get() {
            val coreTypes = listOf(MantraType.DaBei, MantraType.BoRuo, MantraType.WangShen, MantraType.QiFo)
            val counts = coreTypes.map { type ->
                val counter = counters.find { it.mantraType == type }
                if (counter == null || type.mantraGoalCount == 0) 0
                else counter.count / type.mantraGoalCount
            }
            return counts.minOrNull() ?: 0
        }
}
