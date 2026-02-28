package com.nemogz.mantracounter.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val settings by viewModel.settingsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ListItem(
                headlineContent = { Text(stringResource(Res.string.settings_vibrations)) },
                trailingContent = {
                    Switch(
                        checked = settings.vibrationsEnabled,
                        onCheckedChange = { viewModel.onVibrationsToggled(it) }
                    )
                }
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(Res.string.settings_counter_audio)) },
                trailingContent = {
                    Switch(
                        checked = settings.counterAudioEnabled,
                        onCheckedChange = { viewModel.onCounterAudioToggled(it) }
                    )
                }
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(Res.string.settings_little_house_audio)) },
                trailingContent = {
                    Switch(
                        checked = settings.littleHouseAudioEnabled,
                        onCheckedChange = { viewModel.onLittleHouseAudioToggled(it) }
                    )
                }
            )
            HorizontalDivider()

            ListItem(
                headlineContent = { Text(stringResource(Res.string.settings_homework_audio)) },
                trailingContent = {
                    Switch(
                        checked = settings.homeworkAudioEnabled,
                        onCheckedChange = { viewModel.onHomeworkAudioToggled(it) }
                    )
                }
            )
            HorizontalDivider()
            
            ListItem(
                headlineContent = { Text(stringResource(Res.string.settings_theme_mode)) },
                supportingContent = {
                    Column {
                        Text(
                            text = stringResource(Res.string.settings_theme_desc),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val options = com.nemogz.mantracounter.shared.domain.model.ThemeMode.entries
                        val labels = listOf("System", "Light", "Dark")
            
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            options.forEachIndexed { index, option ->
                                SegmentedButton(
                                    selected = settings.themeMode == option,
                                    onClick = { viewModel.onThemeModeChanged(option) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
                                ) {
                                    Text(labels[index])
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}