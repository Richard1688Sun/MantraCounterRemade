package com.nemogz.mantracounter.shared.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nemogz.mantracounter.shared.data.local.entity.CounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters ORDER BY sortOrder ASC")
    fun getAllCounters(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counters WHERE id = :id")
    suspend fun getCounterById(id: String): CounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: CounterEntity)

    @Update
    suspend fun updateCounter(counter: CounterEntity)

    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteCounterById(id: String)
    
    // Batch operations
    @Transaction
    suspend fun updateCounts(ids: List<String>, newCounts: List<Int>) {
        // Room doesn't support list-based update logic easily in one query without a complex CASE statement or mapped updates.
        // A loop here within a Transaction is safe and efficient enough for this scale.
        ids.zip(newCounts).forEach { (id, count) ->
            updateCountQuery(id, count)
        }
    }

    @Update
    suspend fun updateCounters(counters: List<CounterEntity>)

    @Query("UPDATE counters SET count = :count WHERE id = :id")
    suspend fun updateCountQuery(id: String, count: Int)

    @Query("UPDATE counters SET name = :name WHERE id = :id")
    suspend fun updateName(id: String, name: String)
}
