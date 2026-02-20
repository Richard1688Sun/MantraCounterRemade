package com.nemogz.mantracounter.ui.littlehouse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.nemogz.mantracounter.ui.components.GoalProgressBar
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
    var recipientToEdit by remember { mutableStateOf<LittleHouseRecipient?>(null) }
    var recipientToDelete by remember { mutableStateOf<LittleHouseRecipient?>(null) }

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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipient")
            }
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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

                Text(
                    text = "Recipients",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.recipients, key = { it.id }) { recipient ->
                        RecipientCard(
                            recipient = recipient,
                            canAllocate = state.littleHouseCount > 0,
                            onAllocate = { viewModel.onAllocate(recipient.id) },
                            onUnallocate = { viewModel.onUnallocate(recipient.id) },
                            onEdit = { recipientToEdit = recipient },
                            onDelete = { recipientToDelete = recipient }
                        )
                    }
                }
            }
        }
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

    // Delete confirmation
    recipientToDelete?.let { recipient ->
        ConfirmActionDialog(
            title = "Delete Recipient",
            body = "Are you sure you want to delete \"${recipient.name}\"? This action cannot be undone.",
            confirmText = "Delete",
            onConfirm = { viewModel.onDeleteRecipient(recipient.id) },
            onDismiss = { recipientToDelete = null }
        )
    }
}

@Composable
private fun RecipientCard(
    recipient: LittleHouseRecipient,
    canAllocate: Boolean,
    onAllocate: () -> Unit,
    onUnallocate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (recipient.isGoalComplete)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipient.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (recipient.goal > 0) {
                        Text(
                            text = "${recipient.burnedCount} / ${recipient.goal} burned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "${recipient.burnedCount} burned",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    recipient.targetFinishDate?.let { epochDay ->
                        val date = LocalDate.fromEpochDays(epochDay.toInt())
                        val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
                        Text(
                            text = "Target: $monthName ${date.day}, ${date.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    if (!recipient.isDefault) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Progress bar (only if goal > 0)
            if (recipient.goal > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                GoalProgressBar(
                    label = "Burned",
                    current = recipient.burnedCount,
                    goal = recipient.goal,
                    completeColor = MaterialTheme.colorScheme.tertiary,
                    incompleteColor = MaterialTheme.colorScheme.primary,
                    showBorder = false
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAllocate,
                    enabled = canAllocate && !recipient.isGoalComplete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (recipient.isGoalComplete) "Goal Complete ✅" else "Allocate")
                }
                Button(
                    onClick = onUnallocate,
                    enabled = recipient.burnedCount > 0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Unallocate")
                }
            }
        }
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






