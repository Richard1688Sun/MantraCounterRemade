package com.nemogz.mantracounter.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseRecipientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LittleHouseRecipientDao {
    @Query("SELECT * FROM little_house_recipients ORDER BY sortOrder ASC")
    fun getAll(): Flow<List<LittleHouseRecipientEntity>>

    @Query("SELECT * FROM little_house_recipients WHERE id = :id")
    suspend fun getById(id: String): LittleHouseRecipientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LittleHouseRecipientEntity)

    @Query("UPDATE little_house_recipients SET name = :name, goal = :goal, sortOrder = :sortOrder, targetFinishDate = :targetFinishDate WHERE id = :id")
    suspend fun updateDetails(id: String, name: String, goal: Int, sortOrder: Int, targetFinishDate: Long?)

    @Query("DELETE FROM little_house_recipients WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE little_house_recipients SET burnedCount = burnedCount + :amount WHERE id = :id")
    suspend fun incrementBurnedCount(id: String, amount: Int)

    @Query("SELECT COUNT(*) FROM little_house_recipients")
    suspend fun getCount(): Int
}


