package com.nemogz.mantracounter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A reusable labelled progress bar.
 *
 * @param label     Left-hand label (e.g. "Homework", recipient name).
 * @param current   Current count towards the goal.
 * @param goal      The target count. Must be > 0.
 * @param modifier  Modifier applied to the outer Column.
 * @param completeColor  Bar colour when the goal is reached.
 * @param incompleteColor  Bar colour while still in progress.
 * @param showBorder  Whether to draw an outline around the track.
 * @param barHeight  Height of the progress bar.
 * @param valueLabel Optional right-hand label override. Defaults to "current / goal".
 * @param todayCount Number of additions made today, rendered in a distinct color segment.
 * @param todayColor Color for the today-additions segment of the bar.
 */
@Composable
fun GoalProgressBar(
    label: String,
    current: Int,
    goal: Int,
    modifier: Modifier = Modifier,
    completeColor: Color = Color.Unspecified,
    incompleteColor: Color = Color.Unspecified,
    showBorder: Boolean = true,
    barHeight: Dp = 8.dp,
    valueLabel: String? = null,
    todayCount: Int = 0,
    todayColor: Color = Color.Unspecified
) {
    // Resolve sentinel values to theme colours at composition time
    val resolvedComplete   = if (completeColor   == Color.Unspecified) MaterialTheme.colorScheme.tertiary else completeColor
    val resolvedIncomplete = if (incompleteColor == Color.Unspecified) MaterialTheme.colorScheme.primary  else incompleteColor

    val progress = if (goal > 0) (current.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val isComplete = progress >= 1f
    val barColor = if (isComplete) resolvedComplete else resolvedIncomplete

    // Calculate prior progress (everything before today's additions)
    val priorCount = (current - todayCount).coerceAtLeast(0)
    val priorProgress = if (goal > 0) (priorCount.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val hasTodaySegment = todayCount > 0 && todayColor != Color.Unspecified

    // Build the value label
    val displayLabel = valueLabel ?: buildString {
        append("$current / $goal")
        if (todayCount > 0) append(" (+$todayCount today)")
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = displayLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        if (hasTodaySegment) {
            // Two-segment bar using Canvas
            val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            val borderColor = MaterialTheme.colorScheme.outline

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (showBorder) Modifier.border(
                            1.dp,
                            borderColor,
                            RoundedCornerShape(50)
                        ) else Modifier
                    )
            ) {
                val cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)

                // Track background
                drawRoundRect(
                    color = trackColor,
                    size = size,
                    cornerRadius = cornerRadius
                )

                // Prior progress segment (base color)
                if (priorProgress > 0f) {
                    drawRoundRect(
                        color = barColor,
                        size = Size(size.width * priorProgress, size.height),
                        cornerRadius = cornerRadius
                    )
                }

                // Today's additions segment (today color)
                if (progress > priorProgress) {
                    val startX = size.width * priorProgress
                    val endX = size.width * progress
                    drawRect(
                        color = todayColor,
                        topLeft = Offset(startX, 0f),
                        size = Size(endX - startX, size.height)
                    )
                    // Redraw the right end with rounded corner if at the end
                    if (progress >= 1f) {
                        drawRoundRect(
                            color = todayColor,
                            topLeft = Offset(startX, 0f),
                            size = Size(endX - startX, size.height),
                            cornerRadius = cornerRadius
                        )
                    }
                }
            }
        } else {
            // Simple single-segment bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .clip(RoundedCornerShape(50))
                    .then(
                        if (showBorder) Modifier.border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(50)
                        ) else Modifier
                    ),
                color = barColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

