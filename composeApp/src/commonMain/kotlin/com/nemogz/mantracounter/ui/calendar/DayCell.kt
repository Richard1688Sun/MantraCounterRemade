package com.nemogz.mantracounter.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.ui.theme.appColors

@Composable
internal fun DayCell(
    day: CalendarDay,
    activity: DailyActivity?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val isInMonth = day.position == DayPosition.MonthDate

    val hasRecord = activity != null
    val hasCompletedHomework = activity?.activity?.homeworkCompletedDate != null
    val hasConverted = (activity?.activity?.littleHousesConverted ?: 0) > 0
    val hasBurned = (activity?.allocations?.sumOf { (it.endCount - it.startCount).coerceAtLeast(0) } ?: 0) > 0

    // Build list of dot colors to show
    val appColors = MaterialTheme.appColors
    val dots = mutableListOf<Color>()
    if (isInMonth && hasRecord) {
        // Dot 1: outline-grey if no homework done, tertiary-green if homework completed
        dots.add(if (hasCompletedHomework) appColors.homeworkCompletedDot else appColors.homeworkNotCompletedDot)
    }
    if (isInMonth && hasConverted) {
        dots.add(appColors.convertedHouseDot)
    }
    if (isInMonth && hasBurned) {
        dots.add(appColors.burnedHouseDot)
    }

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isInMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = isInMonth, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.day.toString(),
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )

            // Activity indicator dots row
            if (dots.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dots.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else color
                                )
                        )
                    }
                }
            }
        }
    }
}
