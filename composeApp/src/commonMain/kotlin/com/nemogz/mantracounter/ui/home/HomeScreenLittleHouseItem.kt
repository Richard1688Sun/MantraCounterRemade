package com.nemogz.mantracounter.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.ui.components.ConfirmActionDialog

@Composable
fun HomeScreenLittleHouseItem(
    littleHouseCount: Int,
    canConvert: Boolean,
    onConvert: () -> Unit,
    onNavigateToLittleHouse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConvertDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Little House Count: $littleHouseCount", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showConvertDialog = true },
                    enabled = canConvert
                ) {
                    Text("Convert")
                }
                Button(
                    onClick = onNavigateToLittleHouse,
                    enabled = littleHouseCount > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Burn")
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
