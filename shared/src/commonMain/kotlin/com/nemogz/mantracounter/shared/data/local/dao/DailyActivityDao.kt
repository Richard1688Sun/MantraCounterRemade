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

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertActivity(activity: DailyActivityEntity)

    @Update
    suspend fun updateActivity(activity: DailyActivityEntity)

    @Query("SELECT * FROM daily_activity WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getActivitiesBetweenDates(startDate: Long, endDate: Long): List<DailyActivityEntity>

    /** Returns all rows whose homework was performed on [date] (i.e. homeworkCompletedDate == date). */
    @Query("SELECT * FROM daily_activity WHERE homeworkCompletedDate = :date ORDER BY date ASC")
    suspend fun getActivitiesCompletedOnDate(date: Long): List<DailyActivityEntity>

    /** Returns a count of rows whose homework was performed on [date]. */
    @Query("SELECT COUNT(*) FROM daily_activity WHERE homeworkCompletedDate = :date")
    suspend fun countActivitiesCompletedOnDate(date: Long): Int

    @Query("UPDATE daily_activity SET homeworkCompletedDate = :homeworkCompletedDate WHERE date = :date")
    suspend fun updateHomeworkCompletedDate(date: Long, homeworkCompletedDate: Long?)

    @Query("UPDATE daily_activity SET littleHousesConverted = littleHousesConverted + :amount WHERE date = :date")
    suspend fun incrementLittleHousesConverted(date: Long, amount: Int)

    @Query("UPDATE daily_activity SET littleHouseManualIncrease = :manualIncrease WHERE date = :date")
    suspend fun updateLittleHouseManualIncrease(date: Long, manualIncrease: Int)
}
