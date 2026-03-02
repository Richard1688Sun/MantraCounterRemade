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

    @Query("UPDATE mantra_and_homework_details SET endCount = :endCount WHERE `key` = :key")
    suspend fun updateMantraCount(key: String, endCount: Int)

    @Query("UPDATE mantra_and_homework_details SET homeworkGoal = :homeworkGoal WHERE `key` = :key")
    suspend fun updateMantraGoal(key: String, homeworkGoal: Int)

    @Query("UPDATE mantra_and_homework_details SET mantraSortOrder = :mantraSortOrder, mantraName = :mantraName WHERE `key` = :key")
    suspend fun updateMantraDetails(key: String, mantraSortOrder: Int, mantraName: String)

    @Query("DELETE FROM mantra_and_homework_details WHERE dailyActivityDate = :date")
    suspend fun deleteDetailsByDate(date: Long)
}
