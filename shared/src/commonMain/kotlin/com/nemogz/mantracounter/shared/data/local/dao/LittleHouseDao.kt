package com.nemogz.mantracounter.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LittleHouseDao {
    @Query("SELECT * FROM little_house WHERE id = 1")
    fun getLittleHouse(): Flow<LittleHouseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LittleHouseEntity)

    @Query("UPDATE little_house SET count = count + :amount WHERE id = 1")
    suspend fun incrementCount(amount: Int)
    
    @Query("UPDATE little_house SET count = :count WHERE id = 1")
    suspend fun setCount(count: Int)

    @Query("UPDATE little_house SET name = :name WHERE id = 1")
    suspend fun setName(name: String)
}
