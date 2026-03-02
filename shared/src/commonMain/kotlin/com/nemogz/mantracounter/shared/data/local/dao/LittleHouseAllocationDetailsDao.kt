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

    @Query("UPDATE little_house_allocation_details SET endCount = :endCount WHERE `key` = :key")
    suspend fun updateAllocationCount(key: String, endCount: Int)

    @Query("UPDATE little_house_allocation_details SET allocationGoal = :allocationGoal WHERE `key` = :key")
    suspend fun updateAllocationGoal(key: String, allocationGoal: Int)

    @Query("UPDATE little_house_allocation_details SET recipientSortOrder = :recipientSortOrder, recipientTargetFinishDate = :recipientTargetFinishDate, recipientName = :recipientName WHERE `key` = :key")
    suspend fun updateAllocationDetails(key: String, recipientSortOrder: Int, recipientTargetFinishDate: Long?, recipientName: String)

    @Query("DELETE FROM little_house_allocation_details WHERE dailyActivityDate = :date")
    suspend fun deleteDetailsByDate(date: Long)
}
