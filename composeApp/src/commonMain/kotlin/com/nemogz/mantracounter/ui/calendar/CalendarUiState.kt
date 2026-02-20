package com.nemogz.mantracounter.ui.calendar

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val activitiesByDate: Map<LocalDate, DailyActivityEntity> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val recipients: List<LittleHouseRecipient> = emptyList(),
    val isLoading: Boolean = false
) {
    /** Total little houses burned across all recipients */
    val totalBurned: Int get() = recipients.sumOf { it.burnedCount }

    /** Total goal across all recipients (only those with goal > 0) */
    val totalGoal: Int get() = recipients.filter { it.goal > 0 }.sumOf { it.goal }
}
