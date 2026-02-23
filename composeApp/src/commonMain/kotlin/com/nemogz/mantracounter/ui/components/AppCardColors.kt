package com.nemogz.mantracounter.ui.components

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * App-standard [CardColors] using the theme's [MaterialTheme.colorScheme.surfaceContainerLow]
 * as the default container colour.
 *
 * Pass an explicit [containerColor] to override for fixed-colour cards
 * (e.g. [MaterialTheme.colorScheme.primaryContainer] for the LittleHouse header).
 */
@Composable
fun appCardColors(
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
): CardColors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor)

/**
 * App-standard [CardColors] for **selectable / editable** list items.
 *
 * Centralised state → colour mapping:
 * - selected  → [MaterialTheme.colorScheme.primaryContainer]
 * - edit mode → [MaterialTheme.colorScheme.surfaceVariant]
 * - default   → [MaterialTheme.colorScheme.surfaceContainerLow]
 *
 * An optional [selectedColor] / [editColor] / [defaultColor] lets individual
 * call sites override a specific state without duplicating anything else.
 */
@Composable
fun selectableCardColors(
    isSelected: Boolean,
    isEditMode: Boolean,
    isComplete: Boolean = false,
    selectedColor: Color = lerp(MaterialTheme.colorScheme.secondaryContainer, Color.Black, 0.1f),
    editColor: Color    = MaterialTheme.colorScheme.secondaryContainer,
    completeColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    defaultColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
): CardColors = appCardColors(
    containerColor = when {
        isSelected -> selectedColor
        isEditMode -> editColor
        isComplete -> completeColor
        else       -> defaultColor
    },
    contentColor = contentColor
)
