package com.nemogz.mantracounter.shared.domain.repository

import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import kotlinx.coroutines.flow.Flow

interface ILittleHouseRecipientRepository {
    fun getAll(): Flow<List<LittleHouseRecipient>>
    suspend fun getById(id: String): LittleHouseRecipient?
    suspend fun insertLittleHouseRecipient(recipient: LittleHouseRecipient)
    suspend fun updateDetails(id: String, name: String, goal: Int, sortOrder: Int, targetFinishDate: Long?)
    suspend fun deleteById(id: String)
    suspend fun incrementBurnedCount(id: String, amount: Int)
    suspend fun getCount(): Int
}

