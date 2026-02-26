package com.nemogz.mantracounter.shared.data.repository

import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseDao
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseEntity
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LittleHouseRepositoryImpl(
    private val littleHouseDao: LittleHouseDao
) : ILittleHouseRepository {

    override fun getLittleHouseCount(): Flow<Int> {
        return littleHouseDao.getLittleHouse().map { it?.count ?: 0 }
    }

    override fun getLittleHouseName(): Flow<String> {
        return littleHouseDao.getLittleHouse().map { it?.name ?: "Little House" }
    }

    override suspend fun setLittleHouseCount(count: Int) {
        // We use ID=1 for the singleton record. Try updating, if nothing changed, insert
        littleHouseDao.setCount(count)
    }

    override suspend fun setLittleHouseName(name: String) {
        littleHouseDao.setName(name)
    }

    override suspend fun incrementLittleHouseCount(amount: Int) {
        littleHouseDao.incrementCount(amount)
    }

    override suspend fun insertInitialLittleHouseIfEmpty() {
        // If DB has no LittleHouse record, it won't exist.
        // Doing an insert with OnConflictStrategy.REPLACE handles this.
        val existing = littleHouseDao.getLittleHouse().first()
        if (existing == null) {
            littleHouseDao.insert(LittleHouseEntity(id = 1, name = "Little House", count = 0))
        }
    }
}
