package com.nemogz.mantracounter.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.nemogz.mantracounter.ui.util.formatRelativeDate
import com.nemogz.mantracounter.ui.components.ConfirmActionDialog
import com.nemogz.mantracounter.ui.components.appCardColors
import com.nemogz.mantracounter.ui.theme.AppHaptics.LongTap
import io.github.compose.jindong.Jindong
import io.github.compose.jindong.JindongProvider
import kotlinx.datetime.LocalDate

@Composable
fun HomeScreenHomeworkItem(
    missedHomeworkDays: List<Long>,
    canCompleteHomework: Boolean,
    onCatchUpDay: (Long) -> Unit,
    onNavigateToHomework: () -> Unit,
    onShowSnackbar: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pendingCatchUpDay by remember { mutableStateOf<Long?>(null) }
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
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(Res.string.homework_title), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.height(8.dp))
            if (missedHomeworkDays.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.homework_missed_days_msg, missedHomeworkDays.size),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = stringResource(Res.string.homework_all_caught_up),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Complete Homework dropdown
                var showMissedDaysMenu by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { showMissedDaysMenu = true },
                        enabled = missedHomeworkDays.isNotEmpty() && canCompleteHomework
                    ) {
                        Text(stringResource(Res.string.homework_complete_homework))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(Res.string.homework_select_day_desc),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMissedDaysMenu,
                        onDismissRequest = { showMissedDaysMenu = false }
                    ) {
                        missedHomeworkDays.forEachIndexed { index, epochDay ->
                            val dateStr = LocalDate.fromEpochDays(epochDay.toInt()).toString()
                            val relativeStr = formatRelativeDate(epochDay)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(dateStr, style = MaterialTheme.typography.bodyMedium)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.tertiaryContainer
                                        ) {
                                            Text(
                                                text = relativeStr,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    pendingCatchUpDay = epochDay
                                    showMissedDaysMenu = false
                                }
                            )
                            // Divider between items (not after the last one)
                            if (index < missedHomeworkDays.size - 1) {
                                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }
                // Goals navigation button
                OutlinedButton(
                    onClick = onNavigateToHomework,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(stringResource(Res.string.homework_goals_btn))
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

    pendingCatchUpDay?.let { day ->
        val dateStr = LocalDate.fromEpochDays(day.toInt()).toString()
        val snackbarMsg = stringResource(Res.string.homework_snackbar_msg, dateStr)
        ConfirmActionDialog(
            title = stringResource(Res.string.homework_complete_homework),
            body = stringResource(Res.string.homework_confirm_msg, dateStr),
            confirmText = stringResource(Res.string.homework_complete_button),
            onConfirm = {
                confirmDialogTrigger++
                onCatchUpDay(day)
                onShowSnackbar(snackbarMsg)
                pendingCatchUpDay = null
            },
            onDismiss = { pendingCatchUpDay = null }
        )
    }
}
