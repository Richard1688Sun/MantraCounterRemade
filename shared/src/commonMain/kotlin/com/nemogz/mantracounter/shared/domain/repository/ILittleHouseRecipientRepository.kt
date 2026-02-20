package com.nemogz.mantracounter.shared.domain.repository

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import kotlinx.coroutines.flow.Flow

interface ILittleHouseRecipientRepository {
    fun getAll(): Flow<List<LittleHouseRecipient>>
    suspend fun getById(id: String): LittleHouseRecipient?
    suspend fun insert(recipient: LittleHouseRecipient)
    suspend fun update(recipient: LittleHouseRecipient)
    suspend fun deleteById(id: String)
    suspend fun incrementBurnedCount(id: String, amount: Int)
    suspend fun getCount(): Int
}

