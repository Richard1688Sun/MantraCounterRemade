package com.nemogz.mantracounter.shared.data.repository

import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseDao
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseEntity
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LittleHouseRepositoryImpl(
    private val littleHouseDao: LittleHouseDao
) : ILittleHouseRepository {

    override fun getLittleHouseCount(): Flow<Int> {
        return littleHouseDao.getLittleHouseCount().map { it ?: 0 }
    }

    override suspend fun setLittleHouseCount(count: Int) {
        // We use ID=1 for the singleton record
        littleHouseDao.insert(LittleHouseEntity(id = 1, count = count))
    }

    override suspend fun incrementLittleHouseCount(amount: Int) {
        // Create if not exists logic might be needed if DB is empty, 
        // but typically we pre-populate or insert default. 
        // For robustness, let's try update, if row count is 0, insert.
        // Actually, simple way:
        littleHouseDao.incrementCount(amount)
    }
}
