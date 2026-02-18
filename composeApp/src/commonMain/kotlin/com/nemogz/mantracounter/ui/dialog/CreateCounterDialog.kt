package com.nemogz.mantracounter.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun CreateCounterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit // name, initialCount
) {
    var name by remember { mutableStateOf("") }
    var initialCountText by remember { mutableStateOf("0") }
    var error by remember { mutableStateOf<String?>(null) }
    
    // We could add target here if needed, but keeping it simple as per request
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Mantra Counter") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Mantra Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = initialCountText,
                    onValueChange = { 
                        initialCountText = it
                        if (it.toIntOrNull() == null || it.toInt() < 0) {
                            error = "Invalid Number"
                        } else {
                            error = null
                        }
                    },
                    label = { Text("Initial Count (Optional)") },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                 if (error != null) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val initialCount = initialCountText.toIntOrNull()
                    if (name.isNotBlank() && initialCount != null && initialCount >= 0) {
                         onConfirm(name, initialCount)
                    }
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
}
