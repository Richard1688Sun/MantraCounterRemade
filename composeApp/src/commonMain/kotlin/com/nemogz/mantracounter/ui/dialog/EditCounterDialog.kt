package com.nemogz.mantracounter.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.ui.util.UiText // 1. Added Missing Import
import mantracounterremade.composeapp.generated.resources.Res
import mantracounterremade.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCounterDialog(
    counter: Counter,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf(counter.name) }
    var countText by remember { mutableStateOf(counter.count.toString()) }
    var error by remember { mutableStateOf<UiText?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.dialog_edit_counter_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.dialog_mantra_name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = countText,
                    onValueChange = {
                        countText = it
                        val newCount = it.toIntOrNull()
                        error = when {
                            it.isNotEmpty() && newCount == null -> UiText.StringRes(Res.string.lh_invalid_number_error)
                            newCount != null && newCount < 0 -> UiText.StringRes(Res.string.error_negative_count)
                            newCount != null && newCount > com.nemogz.mantracounter.shared.domain.model.CounterConstants.MAX_COUNT ->
                                UiText.StringRes(Res.string.error_max_count, listOf(com.nemogz.mantracounter.shared.domain.model.CounterConstants.MAX_COUNT))
                            else -> null
                        }
                    },
                    label = { Text(stringResource(Res.string.dialog_count_label)) },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Fixed unsafe !! unwrapping using ?.let
                error?.let { currentError ->
                    Text(
                        text = currentError.asString(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = countText.toIntOrNull()
                    if (count != null && count >= 0 && name.isNotBlank()) {
                        onConfirm(name, count)
                    }
                },
                enabled = error == null && name.isNotBlank()
            ) {
                Text(stringResource(Res.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.dialog_cancel))
            }
        }
    )
}