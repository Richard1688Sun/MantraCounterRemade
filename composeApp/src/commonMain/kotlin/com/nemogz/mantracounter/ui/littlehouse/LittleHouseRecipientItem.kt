package com.nemogz.mantracounter.ui.littlehouse

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.ui.components.GoalProgressBar
import com.nemogz.mantracounter.ui.components.SvgImage
import com.nemogz.mantracounter.ui.components.selectableCardColors
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import mantracounterremade.composeapp.generated.resources.Res

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LittleHouseRecipientItem(
    recipient: LittleHouseRecipient,
    isEditMode: Boolean,
    isSelected: Boolean,
    onSelectionToggle: () -> Unit,
    canAllocate: Boolean,
    onAllocate: () -> Unit,
    onUnallocate: () -> Unit,
    onEdit: () -> Unit,
    dragModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    // Jiggle Animation (Rotation)
    val infiniteTransition = rememberInfiniteTransition(label = "jiggle_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Shake Animation (Translation X) for protected items
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val rotationModifier = if (isEditMode) Modifier.rotate(rotation) else Modifier

    // Border for protected (default) items in Edit Mode
    val borderStroke = if (isEditMode && recipient.isDefault) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.error)
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(rotationModifier)
            .then(if (shakeOffset.value != 0f) Modifier.offset(x = shakeOffset.value.dp) else Modifier),
        colors = selectableCardColors(
            isSelected = isSelected,
            isEditMode = isEditMode,
            isComplete = recipient.isGoalComplete
        ),
        border = borderStroke
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isEditMode) dragModifier else Modifier)
                .combinedClickable(
                    onClick = {
                        if (isEditMode) {
                            if (recipient.isDefault) {
                                // Trigger shake for protected item
                                scope.launch {
                                    shakeOffset.snapTo(0f)
                                    for (i in 0..2) {
                                        shakeOffset.animateTo(8f, animationSpec = tween(50))
                                        shakeOffset.animateTo(-8f, animationSpec = tween(50))
                                    }
                                    shakeOffset.animateTo(0f, animationSpec = tween(50))
                                }
                            } else {
                                onSelectionToggle()
                            }
                        }
                    },
                    onLongClick = { onEdit() }
                )
        ) {
            Column(modifier = Modifier.padding(
                start = if (isEditMode) 40.dp else 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )) {
                Text(
                    text = recipient.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (recipient.goal > 0) {
                    // When there's a goal, show only the progress bar (it displays "current / goal")
                    Spacer(modifier = Modifier.height(8.dp))
                    GoalProgressBar(
                        label = "Offered",
                        current = recipient.burnedCount,
                        goal = recipient.goal,
                        showBorder = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    // No goal — just show the burned count as text
                    Text(
                        text = "${recipient.burnedCount} offered",
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

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAllocate,
                        enabled = canAllocate && !recipient.isGoalComplete,
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            if (recipient.isGoalComplete) "Goal Complete ✅" else "Allocate",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Button(
                        onClick = onUnallocate,
                        enabled = recipient.burnedCount > 0,
                        modifier = Modifier.weight(1f).height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Unallocate", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Floating Icons Overlay (Selection/Star)
            if (isEditMode) {
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                ) {
                    if (recipient.isDefault) {
                        SvgImage(
                            resource = Res.getUri("drawable/ic_lotus.svg"),
                            contentDescription = "Lotus Icon",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        RadioButton(
                            selected = isSelected,
                            onClick = null // Handled by parent click
                        )
                    }
                }
            }
        }
    }
}
