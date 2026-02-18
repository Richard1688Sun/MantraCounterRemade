package com.nemogz.mantracounter.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.domain.model.Counter

@Composable
fun EditCounterDialog(
    counter: Counter,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf(counter.name) }
    var countText by remember { mutableStateOf(counter.count.toString()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Counter") },
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
                    value = countText,
                    onValueChange = { 
                        countText = it
                        val newCount = it.toIntOrNull()
                        error = when {
                            it.isNotEmpty() && newCount == null -> "Invalid number"
                            newCount != null && newCount < 0 -> "Negative count not allowed"
                            newCount != null && newCount > com.nemogz.mantracounter.shared.domain.model.CounterConstants.MAX_COUNT -> "Max count is ${com.nemogz.mantracounter.shared.domain.model.CounterConstants.MAX_COUNT}"
                            else -> null
                        }
                    },
                    label = { Text("Count") },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(
                        text = error!!, 
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
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
