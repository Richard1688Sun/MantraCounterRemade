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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nemogz.mantracounter.ui.components.GoalProgressBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import com.nemogz.mantracounter.ui.theme.AppHaptics.ShortTap
import io.github.compose.jindong.Jindong
import io.github.compose.jindong.JindongProvider
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.round

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun CounterDetailScreen(
    counterId: String,
    onBack: () -> Unit,
    viewModel: CounterDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Haptic triggers
    var isNavigatingBack by remember { mutableStateOf(false) }
    var decrementTrigger by remember { mutableStateOf(0) }
    var incrementTrigger by remember { mutableStateOf(0) }

    // Set up the declarative haptic observers
    JindongProvider {
        if (decrementTrigger > 0) {
            Jindong(decrementTrigger) {
                ShortTap()
            }
        }

        if (incrementTrigger > 0) {
            Jindong(incrementTrigger) {
                ShortTap()
            }
        }
    }
    LaunchedEffect(counterId) {
        viewModel.loadCounter(counterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.counter?.name ?: stringResource(Res.string.home_counter_detail_fallback_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isNavigatingBack) {
                            isNavigatingBack = true
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
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
                    onClick = {
                        if (!isNavigatingBack) {
                            decrementTrigger++
                            viewModel.onDecrement()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = stringResource(Res.string.subtract))
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
                        val completingLh = counter.count / lhGoal
                        
                        GoalProgressBar(
                            label = stringResource(Res.string.lh_convertible_houses_label, completingLh),
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
                            label = stringResource(Res.string.homework_title),
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
                        onClick = {
                            if (!isNavigatingBack) {
                                incrementTrigger++
                                viewModel.onIncrement()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
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
