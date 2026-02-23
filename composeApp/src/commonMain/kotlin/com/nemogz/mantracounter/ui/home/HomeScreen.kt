package com.nemogz.mantracounter.ui.home

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.ui.components.EditableItemGrid
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import mantracounterremade.composeapp.generated.resources.Res
import mantracounterremade.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.nemogz.mantracounter.ui.util.getLocalizedMantraName
import kotlin.time.Clock

// Helper class for overwriting standard snackbars
private data class SnackbarEvent(
    val message: String,
    val id: Long = Clock.System.now().toEpochMilliseconds()
)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHomework: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToLittleHouse: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // Trigger day rollover check on every app resume (handles date changes while backgrounded)
    LifecycleResumeEffect(Unit) {
        viewModel.checkDayRollover()
        onPauseOrDispose { }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // State to hold our standard notification events
    var currentSnackbarEvent by remember { mutableStateOf<SnackbarEvent?>(null) }

    // Standard Snackbar Effect (Overwrites itself on every new event)
    LaunchedEffect(currentSnackbarEvent) {
        currentSnackbarEvent?.let { event ->
            snackbarHostState.showSnackbar(event.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            HomeContent(
                state = state,
                onIncrement = viewModel::onIncrementCounter,
                onConvertLittleHouse = viewModel::onConvertLittleHouse,
                onNavigateToLittleHouse = onNavigateToLittleHouse,
                onNavigateToHomework = onNavigateToHomework,
                onCatchUpDay = viewModel::catchUpDay,
                onCounterClick = onNavigateToDetail,
                onToggleEditMode = viewModel::toggleEditMode,
                onMove = viewModel::onMoveCounter,
                onUpdateCounter = viewModel::onUpdateCounter,
                onSelectionMake = viewModel::toggleSelection,
                onClearSelection = viewModel::clearSelection,
                onDeleteSelected = viewModel::deleteSelectedCounters,
                onCreateCounter = { name, initialCount -> viewModel.onCreateCounter(name, 0, initialCount) },
                onNavigateToCalendar = onNavigateToCalendar,
                onNavigateToSettings = onNavigateToSettings,
                // Simply update the event state; the LaunchedEffect handles the rest
                onShowSnackbar = { msg -> currentSnackbarEvent = SnackbarEvent(msg) },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onIncrement: (String) -> Unit,
    onConvertLittleHouse: () -> Unit,
    onNavigateToLittleHouse: () -> Unit,
    onNavigateToHomework: () -> Unit,
    onCatchUpDay: (Long) -> Unit,
    onCounterClick: (String) -> Unit,
    onToggleEditMode: () -> Unit,
    onMove: (Int, Int) -> Unit,
    onUpdateCounter: (String, String, Int) -> Unit,
    onSelectionMake: (String) -> Unit,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onCreateCounter: (String, Int) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Edit/Create Dialog State
    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var counterToEdit by remember { mutableStateOf<Counter?>(null) }

    if (showEditDialog && counterToEdit != null) {
        val counter = counterToEdit!!
        com.nemogz.mantracounter.ui.dialog.EditCounterDialog(
            counter = counter,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, count ->
                onUpdateCounter(counter.id, name, count)
                showEditDialog = false
            }
        )
    }

    if (showCreateDialog) {
        com.nemogz.mantracounter.ui.dialog.CreateCounterDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, initialCount ->
                onCreateCounter(name, initialCount)
                showCreateDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog) {
        // Collect names of selected items
        val selectedNames = state.counters.filter { it.id in state.selectedCounterIds }.joinToString(", ") { it.name }

        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(stringResource(Res.string.home_delete_mantras_title)) },
            text = { Text(stringResource(Res.string.home_delete_mantras_message, selectedNames)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSelected()
                        showDeleteConfirmDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.home_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(stringResource(Res.string.home_cancel))
                }
            }
        )
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Little House Section + Calendar button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            HomeScreenLittleHouseItem(
                littleHouseCount = state.littleHouseCount,
                convertibleCount = state.convertibleLittleHouseCount,
                canConvert = state.canConvertLittleHouse,
                onConvert = onConvertLittleHouse,
                onNavigateToLittleHouse = onNavigateToLittleHouse,
                onShowSnackbar = onShowSnackbar,
                modifier = Modifier.weight(1f)
            )
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onNavigateToCalendar,
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(Res.string.home_tracking_calendar),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(Res.string.settings),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Homework Section
        HomeScreenHomeworkItem(
            missedHomeworkDays = state.missedHomeworkDays,
            canCompleteHomework = state.canCompleteHomework,
            onCatchUpDay = onCatchUpDay,
            onNavigateToHomework = onNavigateToHomework,
            onShowSnackbar = onShowSnackbar
        )

        EditableItemGrid(
            items = state.counters,
            itemKey = { it.id },
            columns = 2,
            title = stringResource(Res.string.home_mantras),
            isEditMode = state.isEditMode,
            hasSelection = state.selectedCounterIds.isNotEmpty(),
            onToggleEditMode = onToggleEditMode,
            onAdd = { showCreateDialog = true },
            onDeleteSelected = { showDeleteConfirmDialog = true },
            onMove = onMove,
            onClearSelection = onClearSelection,
            itemContent = { counter, dragModifier ->
                HomeScreenCounterItem(
                    counter = counter,
                    isEditMode = state.isEditMode,
                    isSelected = state.selectedCounterIds.contains(counter.id),
                    onSelectionToggle = { onSelectionMake(counter.id) },
                    onClick = { onCounterClick(counter.id) },
                    onEdit = {
                        counterToEdit = counter
                        showEditDialog = true
                    },
                    dragModifier = dragModifier,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        )
    }
}