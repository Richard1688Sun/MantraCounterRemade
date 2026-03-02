package com.nemogz.mantracounter.shared.domain.repository

import com.nemogz.mantracounter.shared.domain.model.Counter
import kotlinx.coroutines.flow.Flow

interface ICounterRepository {
    fun getAllCounters(): Flow<List<Counter>>
    suspend fun getCounterById(id: String): Counter?
    suspend fun insertCounter(counter: Counter)
    suspend fun deleteCounter(id: String)
    suspend fun updateCounts(ids: List<String>, newCounts: List<Int>)
    suspend fun updateName(id: String, newName: String)
    suspend fun updateCounter(counter: Counter)
    suspend fun updateCounters(counters: List<Counter>)
    suspend fun updateCount(id: String, count: Int)
    suspend fun updateHomeworkGoal(id: String, homeworkGoal: Int)
}
