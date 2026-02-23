package com.nemogz.mantracounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.nemogz.mantracounter.ui.util.getLocalizedMonthName
import com.nemogz.mantracounter.ui.util.formatFullDate

/**
 * A reusable date-picker dialog backed by kizitonwose's HorizontalCalendar.
 *
 * Features:
 * - ← → arrows for month navigation
 * - Clickable month name → opens a month-picker grid dialog
 * - Clickable year → opens a year-picker grid dialog
 * - Past dates can optionally be disabled
 * - Selected date shown at bottom with a clear button
 *
 * @param title         Dialog title.
 * @param selectedDate  The currently-selected date (nullable).
 * @param onDateSelected Called with the picked date (or null if cleared) when the user taps OK.
 * @param onDismiss     Called when the dialog is cancelled / dismissed.
 * @param allowPast     Whether dates before today can be selected. Defaults to false.
 */
@Composable
fun DatePickerDialog(
    title: String = stringResource(Res.string.picker_select_date),
    selectedDate: LocalDate? = null,
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit,
    allowPast: Boolean = false
) {
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val currentMonth = remember { YearMonth(today.year, today.month) }
    val startMonth = remember {
        if (allowPast) currentMonth.minusMonths(120) else currentMonth
    }
    val endMonth = remember { currentMonth.plusMonths(120) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek) }

    var pickedDate by remember { mutableStateOf(selectedDate) }
    var showYearPicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = selectedDate?.let { YearMonth(it.year, it.month) } ?: currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                HorizontalCalendar(
                    state = calendarState,
                    monthHeader = { calendarMonth ->
                        val yearMonth = calendarMonth.yearMonth
                        val monthName = getLocalizedMonthName(yearMonth.month.ordinal)

                        // ← Month Year →
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    calendarState.animateScrollToMonth(yearMonth.minusMonths(1))
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = stringResource(Res.string.picker_prev_month)
                                )
                            }

                            // Clickable month name + clickable year
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = monthName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { showMonthPicker = true }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${yearMonth.year}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { showYearPicker = true }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    calendarState.animateScrollToMonth(yearMonth.plusMonths(1))
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = stringResource(Res.string.picker_next_month)
                                )
                            }
                        }

                        // Day-of-week headers
                        Row(modifier = Modifier.fillMaxWidth()) {
                            daysOfWeek.forEach { dayOfWeek ->
                                Text(
                                    text = dayOfWeek.name.take(2).uppercase(),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    dayContent = { day ->
                        val isInMonth = day.position == DayPosition.MonthDate
                        val isPast = !allowPast && day.date < today
                        val isEnabled = isInMonth && !isPast
                        val isSelected = pickedDate == day.date
                        val isToday = day.date == today

                        val bgColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isToday -> MaterialTheme.colorScheme.primaryContainer
                            else -> Color.Transparent
                        }
                        val txtColor = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            !isInMonth || isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable(enabled = isEnabled) {
                                    pickedDate = if (pickedDate == day.date) null else day.date
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.date.day.toString(),
                                fontSize = 12.sp,
                                color = txtColor
                            )
                        }
                    }
                )

                // Selected-date indicator row
                if (pickedDate != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val d = pickedDate!!
                        Text(
                            text = stringResource(Res.string.lh_target_label, formatFullDate(d)),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { pickedDate = null }) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(Res.string.lh_clear_date_desc))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDateSelected(pickedDate)
                onDismiss()
            }) {
                Text(stringResource(Res.string.picker_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.picker_cancel))
            }
        }
    )

    // Year picker
    if (showYearPicker) {
        val visibleYear = calendarState.firstVisibleMonth.yearMonth.year
        val visibleMonth = calendarState.firstVisibleMonth.yearMonth.month

        YearPickerDialog(
            currentYear = visibleYear,
            startMonth = startMonth,
            endMonth = endMonth,
            onYearSelected = { selectedYear ->
                showYearPicker = false
                coroutineScope.launch {
                    val target = YearMonth(selectedYear, visibleMonth)
                    val clamped = when {
                        target < startMonth -> startMonth
                        target > endMonth -> endMonth
                        else -> target
                    }
                    calendarState.scrollToMonth(clamped)
                }
            },
            onDismiss = { showYearPicker = false }
        )
    }

    // Month picker
    if (showMonthPicker) {
        val visibleYear = calendarState.firstVisibleMonth.yearMonth.year
        val visibleMonthOrdinal = calendarState.firstVisibleMonth.yearMonth.month.ordinal

        MonthPickerDialog(
            currentMonthOrdinal = visibleMonthOrdinal,
            visibleYear = visibleYear,
            startMonth = startMonth,
            endMonth = endMonth,
            onMonthSelected = { selectedMonthOrdinal ->
                showMonthPicker = false
                coroutineScope.launch {
                    val targetMonth = YearMonth(visibleYear, Month.entries[selectedMonthOrdinal])
                    calendarState.scrollToMonth(targetMonth)
                }
            },
            onDismiss = { showMonthPicker = false }
        )
    }
}


