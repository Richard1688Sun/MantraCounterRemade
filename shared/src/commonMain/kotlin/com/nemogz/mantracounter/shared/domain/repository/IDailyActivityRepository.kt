package com.nemogz.mantracounter.shared.domain.repository

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

interface IDailyActivityRepository {
    suspend fun getFirstActivityDate(): Long?
    suspend fun getMostRecentActivityDate(): Long?
    suspend fun getDailyActivityByDate(date: Long): DailyActivityEntity?
    fun getDailyActivityByDateFlow(date: Long): Flow<DailyActivityEntity?>
    fun getAllActivitiesFlow(): Flow<List<DailyActivityEntity>>
    suspend fun insertOrUpdateActivity(activity: DailyActivityEntity)
    suspend fun getActivitiesBetweenDates(startDate: Long, endDate: Long): List<DailyActivityEntity>
}
