package com.nemogz.mantracounter.ui.components

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * App-standard [CardColors] using the theme's [MaterialTheme.colorScheme.surfaceContainerLow]
 * as the default container colour.
 *
 * Pass an explicit [containerColor] to override for fixed-colour cards
 * (e.g. [MaterialTheme.colorScheme.primaryContainer] for the LittleHouse header).
 */
@Composable
fun appCardColors(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow
): CardColors = CardDefaults.cardColors(containerColor = containerColor)

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
    selectedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    editColor: Color    = MaterialTheme.colorScheme.secondaryContainer,
    defaultColor: Color = MaterialTheme.colorScheme.secondaryContainer
): CardColors = appCardColors(
    containerColor = when {
        isSelected -> selectedColor
        isEditMode -> editColor
        else       -> defaultColor
    }
)
