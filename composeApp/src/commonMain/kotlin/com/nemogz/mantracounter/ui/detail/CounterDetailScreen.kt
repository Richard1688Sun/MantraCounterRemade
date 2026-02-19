package com.nemogz.mantracounter.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
                }
            )
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
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Big Counter Display
                    Text(
                        text = counter.count.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Increment Button
                    Button(
                        onClick = viewModel::onIncrement,
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    ) {
                        Text("Increment", style = MaterialTheme.typography.headlineSmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Homework Settings
                    Text("Settings", style = MaterialTheme.typography.titleLarge)
                    
                    var homeworkInput by remember(counter.homeworkGoal) { 
                        mutableStateOf(counter.homeworkGoal.toString()) 
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = homeworkInput,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() }) {
                                    homeworkInput = it 
                                }
                            },
                            label = { Text("Homework Goal") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Button(
                            onClick = { viewModel.onUpdateHomeworkAmount(homeworkInput) }
                        ) {
                            Text("Save")
                        }
                    }
                    
                    Text(
                        text = "Current Homework Goal: ${counter.homeworkGoal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
