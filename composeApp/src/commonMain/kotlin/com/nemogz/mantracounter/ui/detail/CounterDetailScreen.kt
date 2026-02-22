package com.nemogz.mantracounter.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nemogz.mantracounter.ui.components.GoalProgressBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun CounterDetailScreen(
    counterId: String,
    onBack: () -> Unit,
    viewModel: CounterDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(counterId) {
        viewModel.loadCounter(counterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.counter?.name ?: "Counter Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!state.isLoading && state.counter != null) {
                FloatingActionButton(
                    onClick = viewModel::onDecrement,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Subtract")
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            state.counter?.let { counter ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val showLittleHouseProgress = counter.mantraType.isLittleHouseComponent && counter.mantraType.mantraGoalCount > 0
                    val showHomeworkProgress = counter.homeworkGoal > 0

                    if (showLittleHouseProgress || showHomeworkProgress) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Progress bars near the top
                    if (showLittleHouseProgress) {
                        val lhGoal = counter.mantraType.mantraGoalCount
                        val completedLh = counter.count / lhGoal
                        
                        GoalProgressBar(
                            label = "Convertible Little Houses: $completedLh",
                            current = counter.count % lhGoal,
                            goal = lhGoal,
                            incompleteColor = Color(0xFFFFCA28), // Golden color
                            todayColor = Color(0xFFFFCA28), // Golden color
                            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp),
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (showHomeworkProgress) {
                        GoalProgressBar(
                            label = "Homework",
                            current = counter.count,
                            goal = counter.homeworkGoal,
                            modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp),
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (showLittleHouseProgress || showHomeworkProgress) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Big Counter Button
                    Button(
                        onClick = viewModel::onIncrement,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = androidx.compose.ui.graphics.RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(
                            text = counter.count.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp)
                        )
                    }
                }
            }
        }
    }
}
