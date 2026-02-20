package com.nemogz.mantracounter.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {
    @Query("SELECT MIN(date) FROM daily_activity")
    suspend fun getFirstActivityDate(): Long?

    @Query("SELECT MAX(date) FROM daily_activity")
    suspend fun getMostRecentActivityDate(): Long?

    @Query("SELECT * FROM daily_activity WHERE date = :date")
    suspend fun getDailyActivityByDate(date: Long): DailyActivityEntity?

    @Query("SELECT * FROM daily_activity WHERE date = :date")
    fun getDailyActivityByDateFlow(date: Long): Flow<DailyActivityEntity?>

    @Query("SELECT * FROM daily_activity ORDER BY date DESC")
    fun getAllActivitiesFlow(): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: DailyActivityEntity)

    @Update
    suspend fun updateActivity(activity: DailyActivityEntity)

    @Query("SELECT * FROM daily_activity WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getActivitiesBetweenDates(startDate: Long, endDate: Long): List<DailyActivityEntity>
}
