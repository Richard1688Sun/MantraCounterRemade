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
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.nemogz.mantracounter.ui.components.ConfirmActionDialog
import com.nemogz.mantracounter.ui.components.appCardColors
import com.nemogz.mantracounter.ui.theme.AppHaptics.LongTap
import io.github.compose.jindong.Jindong
import io.github.compose.jindong.JindongProvider

@Composable
fun HomeScreenLittleHouseItem(
    littleHouseCount: Int,
    convertibleCount: Int,
    canConvert: Boolean,
    onConvert: () -> Unit,
    onNavigateToLittleHouse: () -> Unit,
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showConvertDialog by remember { mutableStateOf(false) }
    var confirmDialogTrigger by remember { mutableStateOf(0) }

    // Set up the declarative haptic observers
    JindongProvider {
        if (confirmDialogTrigger > 0) {
            Jindong(confirmDialogTrigger) {
                LongTap()
            }
        }
    }
    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(Res.string.lh_count_label, littleHouseCount),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (convertibleCount > 0) {
                Text(
                    text = stringResource(Res.string.lh_convertible_label, convertibleCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showConvertDialog = true },
                    enabled = canConvert,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                    )
                ) {
                    Text(stringResource(Res.string.lh_convert_button))
                }
                OutlinedButton(
                    onClick = onNavigateToLittleHouse,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    )
                ) {
                    Text(stringResource(Res.string.lh_offerings_button))
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
        val snackbarMsg = stringResource(Res.string.lh_convert_snackbar_msg)
        ConfirmActionDialog(
            title = stringResource(Res.string.lh_convert_dialog_title),
            body = stringResource(Res.string.lh_convert_confirm_msg),
            confirmText = stringResource(Res.string.lh_convert_button),
            onConfirm = {
                confirmDialogTrigger++
                onConvert()
                onShowSnackbar(snackbarMsg)
                showConvertDialog = false
            },
            onDismiss = { showConvertDialog = false }
        )
    }
}
