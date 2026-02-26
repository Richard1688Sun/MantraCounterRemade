package com.nemogz.mantracounter.shared.domain.repository

import kotlinx.coroutines.flow.Flow

interface ILittleHouseRepository {
    fun getLittleHouseCount(): Flow<Int>
    fun getLittleHouseName(): Flow<String>
    suspend fun setLittleHouseCount(count: Int)
    suspend fun setLittleHouseName(name: String)
    suspend fun incrementLittleHouseCount(amount: Int)
    suspend fun insertInitialLittleHouseIfEmpty()
}
