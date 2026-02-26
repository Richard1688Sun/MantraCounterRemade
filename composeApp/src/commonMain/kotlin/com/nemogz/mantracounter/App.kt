package com.nemogz.mantracounter

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nemogz.mantracounter.shared.domain.model.AppSettings
import com.nemogz.mantracounter.shared.domain.model.ThemeMode
import com.nemogz.mantracounter.shared.domain.repository.ISettingsRepository
import com.nemogz.mantracounter.ui.theme.AppTheme
import com.nemogz.mantracounter.ui.theme.LocalVibrationsEnabled
import com.nemogz.mantracounter.ui.detail.CounterDetailScreen
import com.nemogz.mantracounter.ui.home.HomeScreen
import com.nemogz.mantracounter.ui.littlehouse.LittleHouseScreen
import com.nemogz.mantracounter.ui.navigation.Screen
import com.nemogz.mantracounter.ui.settings.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun App() {
    val settingsRepository: ISettingsRepository = koinInject()
    val settings by settingsRepository.getSettings().collectAsState(AppSettings())

    val isSystemDark = isSystemInDarkTheme()
    val darkTheme = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemDark
    }

    CompositionLocalProvider(
        LocalVibrationsEnabled provides settings.vibrationsEnabled
    ) {
        AppTheme(darkTheme = darkTheme) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                
                NavHost(navController = navController, startDestination = Screen.Home) {
                composable<Screen.Home> {
                    HomeScreen(
                        onNavigateToDetail = { counterId ->
                            navController.navigate(Screen.Detail(counterId))
                        },
                        onNavigateToHomework = {
                            navController.navigate(Screen.Homework)
                        },
                        onNavigateToCalendar = {
                            navController.navigate(Screen.Calendar)
                        },
                        onNavigateToLittleHouse = {
                            navController.navigate(Screen.LittleHouse)
                        },
                        onNavigateToSettings = {
                            navController.navigate(Screen.Settings)
                        }
                    )
                }
                
                composable<Screen.Detail> { backStackEntry ->
                    val detail: Screen.Detail = backStackEntry.toRoute()
                    CounterDetailScreen(
                        counterId = detail.counterId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Homework> {
                    com.nemogz.mantracounter.ui.homework.HomeworkScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Calendar> {
                    com.nemogz.mantracounter.ui.calendar.CalendarScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.LittleHouse> {
                    LittleHouseScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<Screen.Settings> {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                }
            }
        }
    }
}
