package com.nemogz.mantracounter.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.ui.components.GoalProgressBar
import com.nemogz.mantracounter.ui.components.appCardColors
import com.nemogz.mantracounter.ui.theme.appColors
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ── Data models for display ──

private data class AllocationDisplayEntry(
    val name: String,
    val start: Int,
    val end: Int,
    val goal: Int
)

private data class MantraRecitedDisplayEntry(
    val name: String,
    val recited: Int,
    val homework: Int,
    val littleHouse: Int,
    val start: Int,
    val end: Int
)

@Composable
internal fun DayDetailPanel(
    selectedDate: LocalDate?,
    activity: DailyActivityEntity?,
    /** Days whose homework was completed on selectedDate (from SQL query). */
    homeworksCompletedHere: List<DailyActivityEntity> = emptyList(),
    /** Called when the user taps a navigation link to jump to another date. */
    onJumpToDate: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = appCardColors(MaterialTheme.colorScheme.surfaceVariant),
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
                        text = "Tap a day to view activity details",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Date header
                Text(
                    text = formatDate(selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (activity == null) {
                    Text(
                        text = "No activity recorded",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // ── Section 1: Homework Status ──
                    val hwCompletionDate = activity.homeworkCompletedDate?.let {
                        LocalDate.fromEpochDays(it.toInt())
                    }

                    if (hwCompletionDate == null) {
                        DetailRow("Homework", "❌ Not Completed")
                    } else if (hwCompletionDate == selectedDate) {
                        // Completed on the same day — no jump link needed
                        DetailRow("Homework", "✅ Completed")
                    } else {
                        // Completed on a different day — show clickable link
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Homework",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f).padding(end = 16.dp)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    append("✅ Done on ")
                                    withStyle(
                                        SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    ) {
                                        append(formatDate(hwCompletionDate))
                                    }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.clickable { onJumpToDate(hwCompletionDate) }
                            )
                        }
                    }

                    // ── Section 2: Homework Detail (only if completed) ──
                    if (hwCompletionDate != null && activity.homeworkDetails.isNotBlank()) {
                        val hwDetails = parseFlatJsonDetails(activity.homeworkDetails)
                        if (hwDetails.isNotEmpty()) {
                            ExpandableSection(title = "Homework Detail") {
                                hwDetails.forEach { (name, count) ->
                                    DetailRow(name, count)
                                }
                            }
                        }
                    }

                    // ── Section 3: Homeworks completed HERE (catch-ups done on this day) ──
                    if (homeworksCompletedHere.isNotEmpty()) {
                        val sortedHomeworks = homeworksCompletedHere.sortedByDescending { it.date }
                        val sectionTitle = "Homeworks Completed (${sortedHomeworks.size})"
                        ExpandableSection(title = sectionTitle, initiallyExpanded = true) {
                            sortedHomeworks.forEach { completedActivity ->
                                val forDate = LocalDate.fromEpochDays(completedActivity.date.toInt())
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "For",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
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
                                                append(formatDate(forDate))
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.clickable { onJumpToDate(forDate) }
                                    )
                                }
                            }
                        }
                    }

                    SectionDivider()

                    // ── Section 4: Little Houses Converted ──
                    DetailRow("Little Houses Converted", activity.littleHousesConverted.toString())

                    SectionDivider()

                    // ── Section 5: Little House Allocations ──
                    val allocEntries = parseAllocationDetailsSnapshot(activity.allocationDetails)

                    if (allocEntries.isNotEmpty()) {
                        val totalAllocatedToday = allocEntries.sumOf { (it.end - it.start).coerceAtLeast(0) }
                        DetailRow("Little Houses Allocated", totalAllocatedToday.toString())

                        ExpandableSection(title = "Allocation Breakdown") {
                            allocEntries.forEach { entry ->
                                if (entry.goal > 0) {
                                    GoalProgressBar(
                                        label = entry.name,
                                        current = entry.end,
                                        goal = entry.goal,
                                        todayCount = (entry.end - entry.start).coerceAtLeast(0),
                                        todayColor = MaterialTheme.colorScheme.secondary,
                                        completeColor = MaterialTheme.colorScheme.tertiary,
                                        incompleteColor = MaterialTheme.colorScheme.primary,
                                        showBorder = false,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                } else {
                                    val todayAllocated = (entry.end - entry.start).coerceAtLeast(0)
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = entry.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f).padding(end = 16.dp)
                                        )
                                        Text(
                                            text = buildString {
                                                append("${entry.end} total")
                                                if (todayAllocated > 0) append(" (+$todayAllocated today)")
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
                        DetailRow("Little Houses Allocated", "0")
                    }

                    SectionDivider()

                    // ── Section 6: Mantra Recited ──
                    val mantraEntries = parseMantraRecitedSnapshot(activity.mantraRecitedDetails)
                    val totalRecited = mantraEntries.sumOf { it.recited }
                    DetailRow("Mantras Recited", totalRecited.toString())

                    if (mantraEntries.isNotEmpty()) {
                        ExpandableSection(title = "Mantra Breakdown") {
                            mantraEntries.forEach { entry ->
                                MantraBreakdownRow(entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Mantra Breakdown Row with color-coded additions/deductions ──

@Composable
private fun MantraBreakdownRow(entry: MantraRecitedDisplayEntry) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        // Header: mantra name and start → end
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 16.dp)
            )
            Text(
                text = "${entry.start} → ${entry.end}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        // Breakdown lines — color-coded
        val appColors = MaterialTheme.appColors
        if (entry.recited > 0) {
            ColoredDetailLine("  Recited", "+${entry.recited}", appColors.recited)
        }
        if (entry.homework > 0) {
            ColoredDetailLine("  Homework", "-${entry.homework}", appColors.homework)
        }
        if (entry.littleHouse > 0) {
            ColoredDetailLine("  Little House", "-${entry.littleHouse}", appColors.littleHouse)
        }
    }
}

@Composable
private fun ColoredDetailLine(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                              else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = 16.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ── Parsers ──

private fun parseAllocationDetailsSnapshot(json: String): List<AllocationDisplayEntry> {
    if (json.isBlank()) return emptyList()
    try {
        val root = Json.parseToJsonElement(json).jsonObject
        return root.entries.map { (name, value) ->
            val obj = value.jsonObject
            AllocationDisplayEntry(
                name = name,
                start = obj["start"]?.jsonPrimitive?.int ?: 0,
                end = obj["end"]?.jsonPrimitive?.int ?: 0,
                goal = obj["goal"]?.jsonPrimitive?.int ?: 0
            )
        }
    } catch (_: Exception) { return emptyList() }
}

private fun parseMantraRecitedSnapshot(json: String): List<MantraRecitedDisplayEntry> {
    if (json.isBlank()) return emptyList()
    try {
        val root = Json.parseToJsonElement(json).jsonObject
        return root.entries.map { (name, value) ->
            val obj = value.jsonObject
            MantraRecitedDisplayEntry(
                name = name,
                recited = obj["recited"]?.jsonPrimitive?.int ?: 0,
                homework = obj["homework"]?.jsonPrimitive?.int ?: 0,
                littleHouse = obj["littleHouse"]?.jsonPrimitive?.int ?: 0,
                start = obj["start"]?.jsonPrimitive?.int ?: 0,
                end = obj["end"]?.jsonPrimitive?.int ?: 0
            )
        }
    } catch (_: Exception) { return emptyList() }
}

private fun parseFlatJsonDetails(json: String): List<Pair<String, String>> {
    if (json.isBlank()) return emptyList()
    try {
        val jsonObj = Json.parseToJsonElement(json).jsonObject
        return jsonObj.entries.map { (key, value) ->
            key to value.jsonPrimitive.content
        }
    } catch (_: Exception) { /* not valid JSON */ }
    // Fallback: "key:value,key:value"
    try {
        return json.split(",").mapNotNull { entry ->
            val parts = entry.split(":", limit = 2)
            if (parts.size == 2) parts[0].trim() to parts[1].trim()
            else null
        }
    } catch (_: Exception) { /* also failed */ }
    return emptyList()
}

internal fun formatDate(date: LocalDate): String {
    val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$monthName ${date.day}, ${date.year}"
}
