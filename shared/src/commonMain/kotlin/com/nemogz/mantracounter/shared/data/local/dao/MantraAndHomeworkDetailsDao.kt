package com.nemogz.mantracounter.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MantraAndHomeworkDetailsDao {
    @Query("SELECT * FROM mantra_and_homework_details WHERE dailyActivityDate = :date")
    suspend fun getDetailsByDate(date: Long): List<MantraAndHomeworkDetailsEntity>

    @Query("SELECT * FROM mantra_and_homework_details WHERE dailyActivityDate = :date")
    fun getDetailsByDateFlow(date: Long): Flow<List<MantraAndHomeworkDetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<MantraAndHomeworkDetailsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: MantraAndHomeworkDetailsEntity)

    @Update
    suspend fun updateDetails(details: List<MantraAndHomeworkDetailsEntity>)

    @Update
    suspend fun updateDetail(detail: MantraAndHomeworkDetailsEntity)

    @Query("DELETE FROM mantra_and_homework_details WHERE dailyActivityDate = :date")
    suspend fun deleteDetailsByDate(date: Long)
}
