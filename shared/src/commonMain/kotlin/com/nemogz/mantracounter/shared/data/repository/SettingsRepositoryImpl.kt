package com.nemogz.mantracounter.shared.data.repository

import com.nemogz.mantracounter.shared.data.local.dao.AppSettingsDao
import com.nemogz.mantracounter.shared.data.local.entity.AppSettingsEntity
import com.nemogz.mantracounter.shared.domain.model.AppSettings
import com.nemogz.mantracounter.shared.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val appSettingsDao: AppSettingsDao
) : ISettingsRepository {
    override fun getSettings(): Flow<AppSettings> {
        return appSettingsDao.getSettings().map { entity ->
            if (entity != null) {
                AppSettings(
                    vibrationsEnabled = entity.vibrationsEnabled,
                    counterAudioEnabled = entity.counterAudioEnabled,
                    littleHouseAudioEnabled = entity.littleHouseAudioEnabled,
                    homeworkAudioEnabled = entity.homeworkAudioEnabled,
                    themeMode = try { com.nemogz.mantracounter.shared.domain.model.ThemeMode.valueOf(entity.themeMode) } catch (e: Exception) { com.nemogz.mantracounter.shared.domain.model.ThemeMode.SYSTEM }
                )
            } else {
                AppSettings()
            }
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        appSettingsDao.upsertSettings(
            AppSettingsEntity(
                vibrationsEnabled = settings.vibrationsEnabled,
                counterAudioEnabled = settings.counterAudioEnabled,
                littleHouseAudioEnabled = settings.littleHouseAudioEnabled,
                homeworkAudioEnabled = settings.homeworkAudioEnabled,
                themeMode = settings.themeMode.name
            )
        )
    }
}
