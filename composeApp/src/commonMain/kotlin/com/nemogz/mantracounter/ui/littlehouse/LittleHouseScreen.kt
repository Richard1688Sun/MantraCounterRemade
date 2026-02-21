package com.nemogz.mantracounter.ui.littlehouse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.ui.components.ConfirmActionDialog
import com.nemogz.mantracounter.ui.components.DatePickerDialog
import com.nemogz.mantracounter.ui.components.EditableItemGrid
import com.nemogz.mantracounter.ui.components.appCardColors
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun LittleHouseScreen(
    viewModel: LittleHouseViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var recipientToEdit by remember { mutableStateOf<LittleHouseRecipient?>(null) }

    // Show error as snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Little House Allocation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                // Little House count header
                Card(
                    colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Available Little Houses",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${state.littleHouseCount}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                EditableItemGrid(
                    items = state.recipients,
                    itemKey = { it.id },
                    columns = 1,
                    title = "Offering Recipients",
                    isEditMode = state.isEditMode,
                    hasSelection = state.selectedRecipientIds.isNotEmpty(),
                    onToggleEditMode = viewModel::toggleEditMode,
                    onAdd = { showCreateDialog = true },
                    onDeleteSelected = { showDeleteConfirmDialog = true },
                    onMove = viewModel::onMoveRecipient,
                    onClearSelection = viewModel::clearSelection,
                    itemContent = { recipient, dragModifier ->
                        LittleHouseRecipientItem(
                            recipient = recipient,
                            isEditMode = state.isEditMode,
                            isSelected = state.selectedRecipientIds.contains(recipient.id),
                            onSelectionToggle = { viewModel.toggleSelection(recipient.id) },
                            canAllocate = state.littleHouseCount > 0,
                            onAllocate = { viewModel.onAllocate(recipient.id) },
                            onUnallocate = { viewModel.onUnallocate(recipient.id) },
                            onEdit = { recipientToEdit = recipient },
                            dragModifier = dragModifier
                        )
                    }
                )
            }
        }
    }

    // Bulk delete confirmation
    if (showDeleteConfirmDialog) {
        val selectedNames = state.recipients
            .filter { it.id in state.selectedRecipientIds }
            .joinToString(", ") { it.name }

        ConfirmActionDialog(
            title = "Delete Recipients?",
            body = "Are you sure you want to delete: $selectedNames?",
            confirmText = "Delete",
            onConfirm = {
                viewModel.deleteSelectedRecipients()
                showDeleteConfirmDialog = false
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    // Create dialog
    if (showCreateDialog) {
        CreateRecipientDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, goal, targetDate ->
                viewModel.onCreateRecipient(name, goal, targetDate)
                showCreateDialog = false
            }
        )
    }

    // Edit dialog
    recipientToEdit?.let { recipient ->
        EditRecipientDialog(
            recipient = recipient,
            onDismiss = { recipientToEdit = null },
            onConfirm = { updated ->
                viewModel.onUpdateRecipient(updated)
                recipientToEdit = null
            }
        )
    }
}

// ─── Create / Edit Dialogs ──────────────────────────────────────────────────

@Composable
private fun CreateRecipientDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, goal: Int, targetFinishDate: Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var goalText by remember { mutableStateOf("0") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Recipient") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = {
                        goalText = it
                        error = if (it.toIntOrNull() == null || it.toInt() < 0) "Invalid number" else null
                    },
                    label = { Text("Goal (0 = no goal)") },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Date picker button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate?.let {
                            val monthName = it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                            "$monthName ${it.day}, ${it.year}"
                        } ?: "No target date",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                    if (selectedDate != null) {
                        IconButton(onClick = { selectedDate = null }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear date")
                        }
                    }
                }

                if (error != null) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalText.toIntOrNull() ?: 0
                    val targetEpochDay = selectedDate?.toEpochDays()?.toLong()
                    onConfirm(name, goal, targetEpochDay)
                },
                enabled = name.isNotBlank() && error == null
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            title = "Select Target Date",
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun EditRecipientDialog(
    recipient: LittleHouseRecipient,
    onDismiss: () -> Unit,
    onConfirm: (LittleHouseRecipient) -> Unit
) {
    var name by remember { mutableStateOf(recipient.name) }
    var goalText by remember { mutableStateOf(recipient.goal.toString()) }
    var selectedDate by remember {
        mutableStateOf(
            recipient.targetFinishDate?.let { LocalDate.fromEpochDays(it.toInt()) }
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Recipient") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = {
                        goalText = it
                        error = if (it.toIntOrNull() == null || it.toInt() < 0) "Invalid number" else null
                    },
                    label = { Text("Goal (0 = no goal)") },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Date picker button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate?.let {
                            val monthName = it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                            "$monthName ${it.day}, ${it.year}"
                        } ?: "No target date",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                    if (selectedDate != null) {
                        IconButton(onClick = { selectedDate = null }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear date")
                        }
                    }
                }

                if (error != null) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalText.toIntOrNull() ?: 0
                    val targetEpochDay = selectedDate?.toEpochDays()?.toLong()
                    onConfirm(recipient.copy(name = name, goal = goal, targetFinishDate = targetEpochDay))
                },
                enabled = name.isNotBlank() && error == null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            title = "Select Target Date",
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false }
        )
    }
}
