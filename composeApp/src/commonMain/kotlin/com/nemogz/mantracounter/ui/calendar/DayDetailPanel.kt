package com.nemogz.mantracounter.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
internal fun DayDetailPanel(
    selectedDate: LocalDate?,
    activity: DailyActivityEntity?,
    recipients: List<LittleHouseRecipient> = emptyList(),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
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
                Text(
                    text = formatDate(selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Recipient burn/goal overview
                if (recipients.isNotEmpty()) {
                    Text(
                        text = "Little House Progress",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    recipients.forEach { recipient ->
                        if (recipient.goal > 0) {
                            com.nemogz.mantracounter.ui.components.GoalProgressBar(
                                label = recipient.name,
                                current = recipient.burnedCount,
                                goal = recipient.goal,
                                completeColor = MaterialTheme.colorScheme.tertiary,
                                incompleteColor = MaterialTheme.colorScheme.primary,
                                showBorder = false,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = recipient.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f).padding(end = 16.dp)
                                )
                                Text(
                                    text = "${recipient.burnedCount} burned",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    // Total row
                    val totalBurned = recipients.sumOf { it.burnedCount }
                    val totalGoal = recipients.filter { it.goal > 0 }.sumOf { it.goal }
                    if (totalGoal > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        com.nemogz.mantracounter.ui.components.GoalProgressBar(
                            label = "Total",
                            current = totalBurned,
                            goal = totalGoal,
                            completeColor = MaterialTheme.colorScheme.tertiary,
                            incompleteColor = MaterialTheme.colorScheme.primary,
                            showBorder = false
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (activity == null) {
                    Text(
                        text = "No activity recorded",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    DetailRow("Little Houses Converted", activity.littleHousesConverted.toString())
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Little Houses Burned", activity.littleHousesBurned.toString())
                    Spacer(modifier = Modifier.height(8.dp))

                    // Burn details by recipient
                    if (activity.littleHouseBurnDetails.isNotBlank()) {
                        val burnDetails = parseBurnDetails(activity.littleHouseBurnDetails)
                        if (burnDetails.isNotEmpty()) {
                            Text(
                                text = "Burn Breakdown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            burnDetails.forEach { (recipientName, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = recipientName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f).padding(end = 16.dp)
                                    )
                                    Text(
                                        text = count,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow(
                        "Homework Completed",
                        if (activity.homeworkCompleted) "✅ Yes" else "❌ No"
                    )

                    if (activity.homeworkDetails.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mantra Breakdown",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val details = parseHomeworkDetails(activity.homeworkDetails)
                        if (details.isEmpty()) {
                            Text(
                                text = "No detail data available",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            details.forEach { (mantraName, count) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mantraName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f).padding(end = 16.dp)
                                    )
                                    Text(
                                        text = count,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
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

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun parseHomeworkDetails(json: String): List<Pair<String, String>> {
    if (json.isBlank()) return emptyList()

    // Try parsing as proper JSON first
    try {
        val jsonObj = Json.parseToJsonElement(json).jsonObject
        return jsonObj.entries.map { (key, value) ->
            key to value.jsonPrimitive.content
        }
    } catch (_: Exception) { /* not valid JSON */ }

    // Fallback: handle legacy "key:value,key:value" format
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

private fun parseBurnDetails(json: String): List<Pair<String, String>> {
    if (json.isBlank()) return emptyList()

    try {
        val jsonObj = Json.parseToJsonElement(json).jsonObject
        return jsonObj.entries.map { (key, value) ->
            key to value.jsonPrimitive.content
        }
    } catch (_: Exception) { /* not valid JSON */ }

    return emptyList()
}

