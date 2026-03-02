package com.nemogz.mantracounter.shared.data.repository

import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseRecipientDao
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.data.mapper.toDomain
import com.nemogz.mantracounter.shared.data.mapper.toEntity
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class LittleHouseRecipientRepositoryImpl(
    private val dao: LittleHouseRecipientDao
) : ILittleHouseRecipientRepository {

    override fun getAll(): Flow<List<LittleHouseRecipient>> {
        return dao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getById(id: String): LittleHouseRecipient? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun insertLittleHouseRecipient(recipient: LittleHouseRecipient) {
        dao.insert(recipient.toEntity())
    }



    override suspend fun updateDetails(id: String, name: String, goal: Int, sortOrder: Int, targetFinishDate: Long?) {
        dao.updateDetails(id, name, goal, sortOrder, targetFinishDate)
    }

    override suspend fun deleteById(id: String) {
        dao.deleteById(id)
    }

    override suspend fun incrementBurnedCount(id: String, amount: Int) {
        dao.incrementBurnedCount(id, amount)
    }

    override suspend fun getCount(): Int {
        return dao.getCount()
    }
}

