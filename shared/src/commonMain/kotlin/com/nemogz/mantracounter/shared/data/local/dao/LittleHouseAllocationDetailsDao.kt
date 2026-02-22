package com.nemogz.mantracounter.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseAllocationDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LittleHouseAllocationDetailsDao {
    @Query("SELECT * FROM little_house_allocation_details WHERE dailyActivityDate = :date")
    suspend fun getDetailsByDate(date: Long): List<LittleHouseAllocationDetailsEntity>

    @Query("SELECT * FROM little_house_allocation_details WHERE dailyActivityDate = :date")
    fun getDetailsByDateFlow(date: Long): Flow<List<LittleHouseAllocationDetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<LittleHouseAllocationDetailsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: LittleHouseAllocationDetailsEntity)

    @Update
    suspend fun updateDetails(details: List<LittleHouseAllocationDetailsEntity>)

    @Update
    suspend fun updateDetail(detail: LittleHouseAllocationDetailsEntity)

    @Query("DELETE FROM little_house_allocation_details WHERE dailyActivityDate = :date")
    suspend fun deleteDetailsByDate(date: Long)
}
