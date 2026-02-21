package com.nemogz.mantracounter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.nemogz.mantracounter.ui.theme.AppTheme
import com.nemogz.mantracounter.ui.detail.CounterDetailScreen
import com.nemogz.mantracounter.ui.home.HomeScreen
import com.nemogz.mantracounter.ui.littlehouse.LittleHouseScreen
import com.nemogz.mantracounter.ui.navigation.Screen

@Composable
fun App() {
    AppTheme {
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
            }
        }
    }
}
