package com.nemogz.mantracounter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemogz.mantracounter.shared.domain.model.AppSettings
import com.nemogz.mantracounter.shared.domain.repository.ISettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {
    val settingsState: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun onVibrationsToggled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsState.value
            settingsRepository.updateSettings(current.copy(vibrationsEnabled = enabled))
        }
    }

    fun onCounterAudioToggled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsState.value
            settingsRepository.updateSettings(current.copy(counterAudioEnabled = enabled))
        }
    }

    fun onLittleHouseAudioToggled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsState.value
            settingsRepository.updateSettings(current.copy(littleHouseAudioEnabled = enabled))
        }
    }

    fun onHomeworkAudioToggled(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsState.value
            settingsRepository.updateSettings(current.copy(homeworkAudioEnabled = enabled))
        }
    }

    fun onThemeModeChanged(mode: com.nemogz.mantracounter.shared.domain.model.ThemeMode) {
        viewModelScope.launch {
            val current = settingsState.value
            settingsRepository.updateSettings(current.copy(themeMode = mode))
        }
    }
}
