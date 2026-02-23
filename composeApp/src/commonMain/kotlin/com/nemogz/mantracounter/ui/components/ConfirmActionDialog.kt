package com.nemogz.mantracounter.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

import mantracounterremade.composeapp.generated.resources.Res
import mantracounterremade.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmActionDialog(
    title: String,
    body: String,
    confirmText: String = stringResource(Res.string.dialog_confirm),
    cancelText: String = stringResource(Res.string.picker_cancel),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(body, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}
