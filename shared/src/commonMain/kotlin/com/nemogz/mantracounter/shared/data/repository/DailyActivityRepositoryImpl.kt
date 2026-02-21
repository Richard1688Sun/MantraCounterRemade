package com.nemogz.mantracounter.shared.data.repository

import com.nemogz.mantracounter.shared.data.local.dao.DailyActivityDao
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.coroutines.flow.Flow

class DailyActivityRepositoryImpl(
    private val dao: DailyActivityDao
) : IDailyActivityRepository {
    override suspend fun getFirstActivityDate(): Long? {
        return dao.getFirstActivityDate()
    }

    override suspend fun getMostRecentActivityDate(): Long? {
        return dao.getMostRecentActivityDate()
    }

    override suspend fun getDailyActivityByDate(date: Long): DailyActivityEntity? {
        return dao.getDailyActivityByDate(date)
    }

    override fun getDailyActivityByDateFlow(date: Long): Flow<DailyActivityEntity?> {
        return dao.getDailyActivityByDateFlow(date)
    }

    override fun getAllActivitiesFlow(): Flow<List<DailyActivityEntity>> {
        return dao.getAllActivitiesFlow()
    }

    override suspend fun insertOrUpdateActivity(activity: DailyActivityEntity) {
        val existing = dao.getDailyActivityByDate(activity.date)
        if (existing == null) {
            dao.insertActivity(activity)
        } else {
            // Room's Insert with REPLACE handles insert/update, but update Activity directly
            dao.updateActivity(activity)
        }
    }

    override suspend fun getActivitiesBetweenDates(startDate: Long, endDate: Long): List<DailyActivityEntity> {
        return dao.getActivitiesBetweenDates(startDate, endDate)
    }

    override suspend fun getActivitiesCompletedOnDate(date: Long): List<DailyActivityEntity> {
        return dao.getActivitiesCompletedOnDate(date)
    }

    override suspend fun countActivitiesCompletedOnDate(date: Long): Int {
        return dao.countActivitiesCompletedOnDate(date)
    }
}
