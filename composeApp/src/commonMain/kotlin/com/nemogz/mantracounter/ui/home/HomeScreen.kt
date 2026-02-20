package com.nemogz.mantracounter.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nemogz.mantracounter.shared.domain.model.Counter
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHomework: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Trigger day rollover check whenever HomeScreen enters composition (e.g., app resume)
    LaunchedEffect(Unit) {
        viewModel.checkDayRollover()
    }

    Scaffold { padding ->
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
                onBurnLittleHouse = viewModel::onBurnLittleHouse,
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
    onBurnLittleHouse: () -> Unit,
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
                // ViewModel.onCreateCounter signature: (name, targetWait=0, initialCount)
                // Here we treat targetWait as 0 for now as requested.
                // We need to pass 3 args if we want initialCount.
                // But the lambda passed in `onCreateCounter` passed to `HomeContent` needs to be updated too.
                // The usage in `HomeScreen` passes `viewModel::onCreateCounter`.
                // `viewModel::onCreateCounter` expects (String, Int, Int).
                // But `HomeContent` defines it as `(String, Int) -> Unit`.
                // I need to update definitions.
                // For this replacement, I will assume I updated the definition.
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
            title = { Text("Delete Mantras?") },
            text = { Text("Are you sure you want to delete these mantras: $selectedNames?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSelected()
                        showDeleteConfirmDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
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
                canConvert = state.canConvertLittleHouse,
                onConvert = onConvertLittleHouse,
                onBurn = onBurnLittleHouse,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onNavigateToCalendar,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Tracking Calendar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Homework Section
        HomeScreenHomeworkItem(
            missedHomeworkDays = state.missedHomeworkDays,
            canCompleteHomework = state.canCompleteHomework,
            onCatchUpDay = onCatchUpDay,
            onNavigateToHomework = onNavigateToHomework
        )

        // Header + Edit/Done + Delete
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mantras", style = MaterialTheme.typography.titleMedium)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (state.isEditMode) {
                     // Delete Button
                    TextButton(
                        onClick = { showDeleteConfirmDialog = true },
                        enabled = state.selectedCounterIds.isNotEmpty(),
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Button(onClick = { 
                    if (state.isEditMode) {
                         onClearSelection()
                    }
                    onToggleEditMode() 
                }) {
                    Text(if (state.isEditMode) "Done" else "Edit")
                }
            }
        }

        val gridState = rememberLazyGridState()
        val reorderableState = rememberReorderableLazyGridState(gridState) { from, to ->
            // Adjust indices if "Add New" item is present? 
            // "Add New" item is not in state.counters, it's artificially added.
            // If we drag "Add New", we should probably disable it.
            // But reorderable library works on keys. "Add New" has a unique key.
            // We need to make sure we don't crash if "Add New" is involved in swap.
            // Ideally, "Add New" is not reorderable.
            if (from.key != "add_new_button" && to.key != "add_new_button") {
                onMove(from.index, to.index)
            }
        }

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.counters, key = { it.id }) { counter ->
                ReorderableItem(reorderableState, key = counter.id) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                    
                    Box(
                        modifier = Modifier
                            .shadow(elevation)
                    ) {
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
                            dragModifier = if (state.isEditMode) Modifier.draggableHandle() else Modifier,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
            
            // "Add New" Item - Only in Edit Mode
            if (state.isEditMode) {
                item(key = "add_new_button") {
                    Card(
                        modifier = Modifier
                            .heightIn(min = 150.dp) // Match min height of counter items
                            .clickable { showCreateDialog = true },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                         border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) // Dashed border would be nice but requires DrawModifier
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().weight(1f)) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Add,
                                contentDescription = "Add New",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}


