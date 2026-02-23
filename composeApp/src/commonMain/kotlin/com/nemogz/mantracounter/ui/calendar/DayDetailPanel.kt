package com.nemogz.mantracounter.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import com.nemogz.mantracounter.ui.util.formatFullDate
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.stringResource
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.ui.components.GoalProgressBar
import com.nemogz.mantracounter.ui.components.appCardColors
import com.nemogz.mantracounter.ui.theme.appColors
import kotlinx.datetime.LocalDate

@Composable
internal fun DayDetailPanel(
    selectedDate: LocalDate?,
    activity: DailyActivity?,
    /** Days whose homework was completed on selectedDate (from SQL query). */
    homeworksCompletedHere: List<DailyActivity> = emptyList(),
    /** Called when the user taps a navigation link to jump to another date. */
    onJumpToDate: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = appCardColors(MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (selectedDate == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.cal_tap_day_msg),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            } else {
                // Date header
                Text(
                    text = formatFullDate(selectedDate),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (activity == null) {
                    Text(
                        text = stringResource(Res.string.cal_no_activity_msg),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    // ── Section 1: Homework Section ──
                    ExpandableSection(title = stringResource(Res.string.homework_title), initiallyExpanded = true) {
                        val hwCompletionDate = activity.activity.homeworkCompletedDate?.let {
                            LocalDate.fromEpochDays(it.toInt())
                        }

                        if (hwCompletionDate == null) {
                            DetailRow(stringResource(Res.string.cal_status_label), stringResource(Res.string.cal_not_completed))
                        } else if (hwCompletionDate == selectedDate) {
                            DetailRow(stringResource(Res.string.cal_status_label), stringResource(Res.string.cal_completed))
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(Res.string.cal_status_label),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f).padding(end = 16.dp)
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        append(stringResource(Res.string.cal_done_on_prefix, ""))
                                        withStyle(
                                            SpanStyle(
                                                color = MaterialTheme.colorScheme.primary,
                                                textDecoration = TextDecoration.Underline,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        ) {
                                            append(formatFullDate(hwCompletionDate))
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.clickable { onJumpToDate(hwCompletionDate) }
                                )
                            }
                        }

                        if (homeworksCompletedHere.isNotEmpty()) {
                            val sortedHomeworks = homeworksCompletedHere.sortedByDescending { it.activity.date }
                            Spacer(modifier = Modifier.height(8.dp))
                            SubExpandableSection(stringResource(Res.string.cal_homeworks_completed, sortedHomeworks.size)) {
                                sortedHomeworks.forEach { completedActivity ->
                                    val forDate = LocalDate.fromEpochDays(completedActivity.activity.date.toInt())
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.cal_for_label),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(
                                                    SpanStyle(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        textDecoration = TextDecoration.Underline,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                ) {
                                                    append(formatFullDate(forDate))
                                                }
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.clickable { onJumpToDate(forDate) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SectionDivider()

                    // ── Section 2: Little House Section ──
                    ExpandableSection(title = stringResource(Res.string.lh_houses_title)) {
                        DetailRow(stringResource(Res.string.cal_converted_today), activity.activity.littleHousesConverted.toString())

                        if (activity.allocations.isNotEmpty()) {
                            val totalAllocatedToday = activity.allocations.sumOf { (it.endCount - it.startCount).coerceAtLeast(0) }
                            DetailRow(stringResource(Res.string.cal_allocated_today), totalAllocatedToday.toString())

                            Spacer(modifier = Modifier.height(8.dp))
                            SubExpandableSection(stringResource(Res.string.cal_allocation_breakdown)) {
                                activity.allocations.forEach { entry ->
                                    if (entry.allocationGoal > 0) {
                                        GoalProgressBar(
                                            label = entry.recipientName,
                                            current = entry.endCount,
                                            goal = entry.allocationGoal,
                                            todayCount = (entry.endCount - entry.startCount).coerceAtLeast(0),
                                            modifier = Modifier.padding(vertical = 2.dp),
                                        )
                                    } else {
                                        val todayAllocated = (entry.endCount - entry.startCount).coerceAtLeast(0)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = entry.recipientName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f).padding(end = 16.dp)
                                            )
                                            Text(
                                                text = buildString {
                                                append(stringResource(Res.string.cal_total_suffix, entry.endCount))
                                                if (todayAllocated > 0) append(stringResource(Res.string.cal_today_added_suffix, todayAllocated))
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            DetailRow(stringResource(Res.string.cal_allocated_today), "0")
                        }
                    }

                    SectionDivider()

                    // ── Section 3: Mantra Section ──
                    ExpandableSection(title = stringResource(Res.string.home_mantras)) {
                        // We count only newly recited amounts for the total
                        val totalRecitedThisDay = activity.mantras.sumOf { (it.endCount - it.startCount).coerceAtLeast(0) }
                        DetailRow(stringResource(Res.string.cal_total_net_mantras), totalRecitedThisDay.toString())

                        if (activity.mantras.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            SubExpandableSection(stringResource(Res.string.cal_mantra_breakdown)) {
                                activity.mantras.forEach { entry ->
                                    val hwCompletionDate = activity.activity.homeworkCompletedDate?.let {
                                        LocalDate.fromEpochDays(it.toInt())
                                    }
                                    val homeworkCompletedToday = hwCompletionDate == selectedDate

                                    MantraBreakdownRow(
                                        name = entry.mantraName,
                                        start = entry.startCount,
                                        end = entry.endCount,
                                        homeworkGoal = entry.homeworkGoal,
                                        littleHousesConvertedToday = activity.activity.littleHousesConverted,
                                        homeworkCompletedToday = homeworkCompletedToday
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Mantra Breakdown Row ──

@Composable
private fun MantraBreakdownRow(name: String, start: Int, end: Int, homeworkGoal: Int, littleHousesConvertedToday: Int, homeworkCompletedToday: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        // Header: mantra name and start → end
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 16.dp)
            )
            Text(
                text = "$start → $end",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        val appColors = MaterialTheme.appColors
        val breakdownIndent = 16.dp
        
        val netChange = end - start
        
        // Reverse engineer the breakdown
        var littleHouseDeductions = 0
        if (littleHousesConvertedToday > 0) {
           val perHouseGoal = when(name) {
               "Great Compassion Mantra" -> 27
               "Heart Sutra" -> 49
               "Amitabha Pure Land Rebirth Mantra" -> 84
               "Sapta Atita Tathagata Mantra" -> 87
               else -> 0
           }
           littleHouseDeductions = perHouseGoal * littleHousesConvertedToday
        }
        
        val homeworkDeductions = if (homeworkCompletedToday) homeworkGoal else 0
        
        val totalDeductions = littleHouseDeductions + homeworkDeductions
        val grossRecited = netChange + totalDeductions

        // Show gross recited if it's != 0 (can be negative if setting a smaller count explicitly) or if there were deductions
        if (grossRecited != 0) {
            val sign = if (grossRecited > 0) "+" else ""
            val color = if (grossRecited > 0) appColors.recitedMantraDot else appColors.homeworkDeductionDot
            val rowColor = if (grossRecited > 0) appColors.recitedMantraRow else appColors.homeworkDeductionRow
            ColoredDetailLine(stringResource(Res.string.cal_gross_recited), "$sign$grossRecited", color, rowColor, indent = breakdownIndent)
        }
        
        if (homeworkDeductions > 0) {
            ColoredDetailLine(stringResource(Res.string.cal_homework_deduction), "-$homeworkDeductions", appColors.homeworkDeductionDot, appColors.homeworkDeductionRow, indent = breakdownIndent)
        }
        
        if (littleHouseDeductions > 0) {
            ColoredDetailLine(stringResource(Res.string.cal_lh_deduction), "-$littleHouseDeductions", appColors.homeworkDeductionDot, appColors.homeworkDeductionRow, indent = breakdownIndent)
        }
        
        if (netChange != 0) {
           val sign = if (netChange > 0) "+" else ""
           ColoredDetailLine(stringResource(Res.string.cal_net_change), "$sign$netChange", if (netChange > 0) appColors.recitedMantraDot else appColors.homeworkDeductionDot, Color.Transparent, indent = breakdownIndent)
        }
    }
}

@Composable
private fun ColoredDetailLine(label: String, value: String, valueColor: Color, backgroundColor: Color = Color.Transparent, indent: Dp = 0.dp) {
    Row(
        modifier = Modifier.fillMaxWidth().background(backgroundColor).padding(vertical = 1.dp).padding(start = indent),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

// ── Reusable composables ──

@Composable
private fun SectionDivider() {
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge,
    titleColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = titleStyle,
                fontWeight = FontWeight.SemiBold,
                color = titleColor
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                              else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) stringResource(Res.string.cal_collapse_desc) else stringResource(Res.string.cal_expand_desc),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    labelColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = labelStyle,
            color = labelColor,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = 16.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = labelColor
        )
    }
}


@Composable
private fun SubExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    ExpandableSection(
        title = title,
        modifier = modifier.padding(bottom = 4.dp),
        initiallyExpanded = initiallyExpanded,
        titleStyle = MaterialTheme.typography.titleMedium,
        titleColor = MaterialTheme.colorScheme.onSecondaryContainer,
        content = content
    )
}
