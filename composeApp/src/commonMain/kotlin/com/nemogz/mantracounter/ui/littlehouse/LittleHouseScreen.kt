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
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.ui.components.ConfirmActionDialog
import com.nemogz.mantracounter.ui.components.DatePickerDialog
import com.nemogz.mantracounter.ui.components.EditableItemGrid
import com.nemogz.mantracounter.ui.components.appCardColors
import com.nemogz.mantracounter.ui.theme.AppHaptics.LongTap
import io.github.compose.jindong.Jindong
import io.github.compose.jindong.JindongProvider
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import com.nemogz.mantracounter.ui.theme.LocalVibrationsEnabled
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.time.Clock

private data class SnackbarEvent(
    val messageRes: StringResource,
    val formatArg: String = "",
    val id: Long = Clock.System.now().toEpochMilliseconds()
)

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun LittleHouseScreen(
    viewModel: LittleHouseViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var allocationTrigger by remember { mutableStateOf(0) }
    var deallocationTrigger by remember { mutableStateOf(0) }

    var currentSnackbarEvent by remember { mutableStateOf<SnackbarEvent?>(null) }
    val isVibrationsEnabled = LocalVibrationsEnabled.current

    JindongProvider {
        if (allocationTrigger > 0) {
            Jindong(allocationTrigger) {
                LongTap(isVibrationsEnabled)
            }
        }

        if (deallocationTrigger > 0) {
            Jindong(deallocationTrigger) {
                LongTap(isVibrationsEnabled)
            }
        }
    }

    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var recipientToEdit by remember { mutableStateOf<LittleHouseRecipient?>(null) }

    val errorMsg = state.error?.asString()
    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(currentSnackbarEvent) {
        currentSnackbarEvent?.let { event ->
            val message = getString(event.messageRes, event.formatArg)
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.lh_allocation_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
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
                Card(
                    colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(Res.string.lh_available_title),
                            style = MaterialTheme.typography.headlineSmall,
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
                    title = stringResource(Res.string.lh_recipients_title),
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
                            onAllocate = {
                                allocationTrigger++
                                viewModel.onAllocate(recipient.id)
                                currentSnackbarEvent = SnackbarEvent(Res.string.lh_snackbar_allocate, recipient.name)
                            },
                            onUnallocate = {
                                deallocationTrigger++
                                viewModel.onUnallocate(recipient.id)
                                currentSnackbarEvent = SnackbarEvent(Res.string.lh_snackbar_unallocate, recipient.name)
                            },
                            onEdit = { recipientToEdit = recipient },
                            dragModifier = dragModifier
                        )
                    }
                )
            }
        }
    }

    if (showDeleteConfirmDialog) {
        val selectedRecipients = state.recipients.filter { it.id in state.selectedRecipientIds }

        val localizedNamesList = mutableListOf<String>()
        for (recipient in selectedRecipients) {
            localizedNamesList.add(recipient.name)
        }
        val selectedNames = localizedNamesList.joinToString(", ")

        ConfirmActionDialog(
            title = stringResource(Res.string.lh_dialog_delete_title),
            body = stringResource(Res.string.lh_dialog_delete_msg, selectedNames),
            confirmText = stringResource(Res.string.home_delete),
            onConfirm = {
                viewModel.deleteSelectedRecipients()
                showDeleteConfirmDialog = false
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }

    if (showCreateDialog) {
        CreateRecipientDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, goal, targetDate ->
                viewModel.onCreateRecipient(name, goal, targetDate)
                showCreateDialog = false
            }
        )
    }

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
    var error by remember { mutableStateOf<StringResource?>(null) } // Fixed: Now a StringResource

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.lh_new_recipient_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.lh_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = {
                        goalText = it
                        // Fixed: Assign the Resource ID directly, no stringResource() call here
                        error = if (it.toIntOrNull() == null || it.toInt() < 0) Res.string.lh_invalid_number_error else null
                    },
                    label = { Text(stringResource(Res.string.lh_goal_label)) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate?.let {
                            val monthName = it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                            "$monthName ${it.day}, ${it.year}"
                        } ?: stringResource(Res.string.lh_no_target_date),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(Res.string.lh_pick_date_desc))
                    }
                    if (selectedDate != null) {
                        IconButton(onClick = { selectedDate = null }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(Res.string.lh_clear_date_desc))
                        }
                    }
                }

                // Fixed: Translate the stored resource ID when drawing the UI
                error?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalText.toIntOrNull() ?: 0
                    val targetEpochDay = selectedDate?.toEpochDays()
                    onConfirm(name, goal, targetEpochDay)
                },
                enabled = name.isNotBlank() && error == null
            ) {
                Text(stringResource(Res.string.lh_create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.lh_cancel_button))
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            title = stringResource(Res.string.lh_select_target_date_title),
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
    var error by remember { mutableStateOf<StringResource?>(null) } // Fixed: Now a StringResource

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.lh_edit_recipient_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.lh_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = {
                        goalText = it
                        // Fixed: Assign the Resource ID directly
                        error = if (it.toIntOrNull() == null || it.toInt() < 0) Res.string.lh_invalid_number_error else null
                    },
                    label = { Text(stringResource(Res.string.lh_goal_label)) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate?.let {
                            val monthName = it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }
                            "$monthName ${it.day}, ${it.year}"
                        } ?: stringResource(Res.string.lh_no_target_date),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = stringResource(Res.string.lh_pick_date_desc))
                    }
                    if (selectedDate != null) {
                        IconButton(onClick = { selectedDate = null }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(Res.string.lh_clear_date_desc))
                        }
                    }
                }

                // Fixed: Translate the stored resource ID when drawing the UI
                error?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalText.toIntOrNull() ?: 0
                    val targetEpochDay = selectedDate?.toEpochDays()
                    onConfirm(recipient.copy(name = name, goal = goal, targetFinishDate = targetEpochDay))
                },
                enabled = name.isNotBlank() && error == null
            ) {
                Text(stringResource(Res.string.lh_save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.lh_cancel_button))
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            title = stringResource(Res.string.lh_select_target_date_title),
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onDismiss = { showDatePicker = false }
        )
    }
}