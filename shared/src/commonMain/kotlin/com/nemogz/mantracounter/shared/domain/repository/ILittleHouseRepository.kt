package com.nemogz.mantracounter.shared.domain.repository

import kotlinx.coroutines.flow.Flow

interface ILittleHouseRepository {
    fun getLittleHouseCount(): Flow<Int>
    suspend fun setLittleHouseCount(count: Int)
    suspend fun incrementLittleHouseCount(amount: Int)
}
