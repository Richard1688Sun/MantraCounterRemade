package com.nemogz.mantracounter.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.time.Clock
import com.nemogz.mantracounter.ui.theme.appColors

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }

    // Refresh data whenever this screen is (re)entered
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val currentMonth = remember { YearMonth(today.year, today.month) }
    val startMonth = remember { currentMonth.minusMonths(120) }
    val endMonth = remember { currentMonth.plusMonths(120) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek) }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val coroutineScope = rememberCoroutineScope()

    // Dialog states
    var showYearPicker by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }

    // Fix: use ordinal+1 (1-based) instead of bare .ordinal (0-based) to prevent crash
    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.firstVisibleMonth }.collect { month ->
            viewModel.onMonthChanged(month.yearMonth.year, month.yearMonth.month.ordinal + 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.home_tracking_calendar)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp)
            ) {
                HorizontalCalendar(
                    state = calendarState,
                    monthHeader = { calendarMonth ->
                        val yearMonth = calendarMonth.yearMonth
                        val monthName = com.nemogz.mantracounter.ui.util.getLocalizedMonthName(yearMonth.month.ordinal + 1)

                        // Navigation row: ← Month Year →
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Previous month arrow
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    calendarState.animateScrollToMonth(
                                        yearMonth.minusMonths(1)
                                    )
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = stringResource(Res.string.cal_prev_month)
                                )
                            }

                            // Clickable month name + clickable year
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = monthName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { showMonthPicker = true }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${yearMonth.year}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { showYearPicker = true }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            // Next month arrow
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    calendarState.animateScrollToMonth(
                                        yearMonth.plusMonths(1)
                                    )
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = stringResource(Res.string.cal_next_month)
                                )
                            }
                        }

                        // Day-of-week labels
                        Row(modifier = Modifier.fillMaxWidth()) {
                            daysOfWeek.forEach { dayOfWeek ->
                                Text(
                                    text = dayOfWeek.name.take(3).lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    },
                    dayContent = { day ->
                        val date = day.date
                        val activity = state.activitiesByDate[date]
                        val isSelected = state.selectedDate == date
                        val isToday = date == today

                        DayCell(
                            day = day,
                            activity = activity,
                            isSelected = isSelected,
                            isToday = isToday,
                            onClick = {
                                if (day.position == DayPosition.MonthDate) {
                                    viewModel.onDaySelected(date)
                                }
                            }
                        )
                    }
                )

                // Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = MaterialTheme.appColors
                    LegendDualItem(
                        noColor = colors.homeworkNotCompletedDot,
                        yesColor = colors.homeworkCompletedDot,
                        noLabel = stringResource(Res.string.cal_legend_no_hw),
                        yesLabel = stringResource(Res.string.cal_legend_hw_done)
                    )
                    LegendItem(color = colors.convertedHouseDot, label = stringResource(Res.string.cal_legend_converted))
                    LegendItem(color = colors.burnedHouseDot, label = stringResource(Res.string.cal_legend_burned))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detail panel for selected day
                DayDetailPanel(
                    selectedDate = state.selectedDate,
                    activity = state.selectedDate?.let { state.activitiesByDate[it] },
                    homeworksCompletedHere = state.homeworksCompletedOnSelectedDate,
                    onJumpToDate = { targetDate ->
                        viewModel.onDaySelected(targetDate)
                        coroutineScope.launch {
                            calendarState.animateScrollToMonth(
                                YearMonth(targetDate.year, targetDate.month)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }

    // Year picker dialog
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

    // Month picker dialog
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
