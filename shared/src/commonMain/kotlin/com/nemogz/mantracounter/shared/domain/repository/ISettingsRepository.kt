package com.nemogz.mantracounter.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import com.nemogz.mantracounter.shared.domain.model.AppSettings

interface ISettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
}
