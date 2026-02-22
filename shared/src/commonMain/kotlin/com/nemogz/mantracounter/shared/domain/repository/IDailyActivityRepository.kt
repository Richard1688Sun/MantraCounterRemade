package com.nemogz.mantracounter.shared.domain.repository

import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import kotlinx.coroutines.flow.Flow

interface IDailyActivityRepository {
    suspend fun getFirstActivityDate(): Long?
    suspend fun getMostRecentActivityDate(): Long?
    suspend fun getDailyActivityByDate(date: Long): DailyActivity?
    fun getDailyActivityByDateFlow(date: Long): Flow<DailyActivity?>
    fun getAllActivitiesFlow(): Flow<List<DailyActivity>>
    suspend fun insertOrUpdateActivity(activity: DailyActivity)
    suspend fun getActivitiesBetweenDates(startDate: Long, endDate: Long): List<DailyActivity>
    /** Returns all activities whose homework was completed on [date]. */
    suspend fun getActivitiesCompletedOnDate(date: Long): List<DailyActivity>
    /** Returns a count of activities whose homework was completed on [date]. */
    suspend fun countActivitiesCompletedOnDate(date: Long): Int
}
