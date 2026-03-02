package com.nemogz.mantracounter.shared.data.repository

import com.nemogz.mantracounter.shared.data.local.dao.CounterDao
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity
import com.nemogz.mantracounter.shared.data.local.entity.toDomain
import com.nemogz.mantracounter.shared.data.local.entity.toEntity
import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CounterRepositoryImpl(
    private val counterDao: CounterDao
) : ICounterRepository {

    override fun getAllCounters(): Flow<List<Counter>> {
        return counterDao.getAllCounters().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCounterById(id: String): Counter? {
        return counterDao.getCounterById(id)?.toDomain()
    }

    override suspend fun insertCounter(counter: Counter) {
        counterDao.insertCounter(counter.toEntity())
    }



    override suspend fun deleteCounter(id: String) {
        counterDao.deleteCounterById(id)
    }

    override suspend fun updateCounts(ids: List<String>, newCounts: List<Int>) {
        counterDao.updateCounts(ids, newCounts)
    }

    override suspend fun updateName(id: String, newName: String) {
        counterDao.updateName(id, newName)
    }

    override suspend fun updateCounter(counter: Counter) {
        counterDao.updateCounter(counter.toEntity())
    }

    override suspend fun updateCounters(counters: List<Counter>) {
        counterDao.updateCounters(counters.map { it.toEntity() })
    }

    override suspend fun updateCount(id: String, count: Int) {
        counterDao.updateCountQuery(id, count)
    }

    override suspend fun updateHomeworkGoal(id: String, homeworkGoal: Int) {
        counterDao.updateHomeworkGoal(id, homeworkGoal)
    }
}
