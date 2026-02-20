package com.nemogz.mantracounter.ui.home

import androidx.compose.ui.graphics.Color

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.domain.model.Counter
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.heightIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.unit.sp
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun HomeScreenCounterItem(
    counter: Counter,
    isEditMode: Boolean,
    isSelected: Boolean,
    onSelectionToggle: () -> Unit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    dragModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    val elevation by animateDpAsState(if (isEditMode) 8.dp else 2.dp)
    
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
    
    // Determine if we can select/edit this counter (Protected logic)
    val isProtected = counter.mantraType.isLittleHouseComponent

    // Border for protected items in Edit Mode
    val borderStroke = if (isEditMode && isProtected) {
        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error)
    } else null

    Card(
        modifier = modifier
            .then(rotationModifier)
            .then(if (shakeOffset.value != 0f) Modifier.offset(x = shakeOffset.value.dp) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) 
                 else if (isEditMode) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) 
                 else CardDefaults.cardColors(),
        border = borderStroke
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isEditMode) dragModifier else Modifier)
                .combinedClickable(
                    onClick = {
                        if (isEditMode) {
                            if (isProtected) {
                                // Trigger shake
                                scope.launch {
                                    // Reset if running
                                    shakeOffset.snapTo(0f)
                                    // Shake sequence
                                    for (i in 0..2) {
                                        shakeOffset.animateTo(8f, animationSpec = tween(50))
                                        shakeOffset.animateTo(-8f, animationSpec = tween(50))
                                    }
                                    shakeOffset.animateTo(0f, animationSpec = tween(50))
                                }
                            } else {
                                onSelectionToggle()
                            }
                        } else {
                            onClick()
                        }
                    },
                    onLongClick = {
                        onEdit()
                    }
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Spacer for top floating icons if needed, or just padding
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = counter.name, 
                    style = MaterialTheme.typography.bodyLarge, 
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Count Display
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val countStr = counter.count.toString()
                    val countStyle = MaterialTheme.typography.displaySmall

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BasicText(
                            text = countStr,
                            style = countStyle.copy(color = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f, fill = false), // Allow shrinking but don't force fill
                            autoSize = TextAutoSize.StepBased(minFontSize = 12.sp, maxFontSize = MaterialTheme.typography.displaySmall.fontSize),
                            maxLines = 1,
                            softWrap = false,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        // Mantra Goal (Little House threshold) - Reverted to text
                        if (counter.mantraType.mantraGoalCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "/ ${counter.mantraType.mantraGoalCount}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp) // Adjusted baseline
                            )
                        }
                    }
                }

                // Progress Bars Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                ) {
                    // Homework Goal Progress
                    com.nemogz.mantracounter.ui.components.GoalProgressBar(
                        label = "Homework",
                        current = counter.count,
                        goal = counter.homeworkGoal,
                        valueLabel = "${counter.homeworkGoal}"
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Floating Icons Overlay (Selection/Star)
            Box(modifier = Modifier.fillMaxSize()) {
                if (isEditMode) {
                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    ) {
                        if (isProtected) {
                                Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                contentDescription = "Protected",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                             androidx.compose.material3.RadioButton(
                                selected = isSelected,
                                onClick = null // Handled by parent click
                            )
                        }
                    }
                }
            }
        }
    }
}






