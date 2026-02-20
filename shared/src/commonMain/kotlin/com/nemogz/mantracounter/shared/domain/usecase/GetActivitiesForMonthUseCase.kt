package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.LocalDate

class GetActivitiesForMonthUseCase(
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Returns a map of LocalDate -> DailyActivityEntity for all days with recorded activity
     * in the given year/month.
     */
    suspend operator fun invoke(year: Int, month: Int): Map<LocalDate, DailyActivityEntity> {
        val firstDay = LocalDate(year, month, 1)
        val lastDay = if (month == 12) {
            LocalDate(year + 1, 1, 1).toEpochDays() - 1
        } else {
            LocalDate(year, month + 1, 1).toEpochDays() - 1
        }

        val startEpochDay = firstDay.toEpochDays().toLong()
        val endEpochDay = lastDay.toLong()

        val activities = dailyActivityRepository.getActivitiesBetweenDates(startEpochDay, endEpochDay)

        return activities.associateBy { activity ->
            LocalDate.fromEpochDays(activity.date.toInt())
        }
    }
}
