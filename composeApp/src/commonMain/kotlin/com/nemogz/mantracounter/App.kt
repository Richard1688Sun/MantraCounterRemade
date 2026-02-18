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
import com.nemogz.mantracounter.ui.detail.CounterDetailScreen
import com.nemogz.mantracounter.ui.home.HomeScreen
import com.nemogz.mantracounter.ui.navigation.Screen

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            
            NavHost(navController = navController, startDestination = Screen.Home) {
                composable<Screen.Home> {
                    HomeScreen(
                        onNavigateToDetail = { counterId ->
                            navController.navigate(Screen.Detail(counterId))
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
            }
        }
    }
}
