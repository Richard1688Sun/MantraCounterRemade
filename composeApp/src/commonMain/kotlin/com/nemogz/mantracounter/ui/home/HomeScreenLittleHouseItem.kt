package com.nemogz.mantracounter.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.ui.components.ConfirmActionDialog
import com.nemogz.mantracounter.ui.components.appCardColors

@Composable
fun HomeScreenLittleHouseItem(
    littleHouseCount: Int,
    convertibleCount: Int,
    canConvert: Boolean,
    onConvert: () -> Unit,
    onNavigateToLittleHouse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConvertDialog by remember { mutableStateOf(false) }

    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Little House Count: $littleHouseCount",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (convertibleCount > 0) {
                Text(
                    text = "Convertible: $convertibleCount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showConvertDialog = true },
                    enabled = canConvert,
                    colors = ButtonDefaults.buttonColors (
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                ) {
                    Text("Convert")
                }
                OutlinedButton(
                    onClick = onNavigateToLittleHouse,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    )
                ) {
                    Text("Offerings")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showConvertDialog) {
        ConfirmActionDialog(
            title = "Convert Little House",
            body = "This will deduct mantra counts and convert them into 1 Little House. Are you sure?",
            confirmText = "Convert",
            onConfirm = onConvert,
            onDismiss = { showConvertDialog = false }
        )
    }
}
