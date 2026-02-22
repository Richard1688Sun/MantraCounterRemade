package com.nemogz.mantracounter.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

/**
 * A reusable editable, reorderable grid/list of items.
 *
 * Provides a header row with a title, Edit/Done toggle, and Add/Delete buttons
 * visible only in edit mode. Items are rendered via [itemContent] and are
 * drag-reorderable when in edit mode.
 *
 * @param T             The item type.
 * @param items         The list of items to display.
 * @param itemKey       Lambda returning a unique, stable key for each item.
 * @param columns       Number of grid columns (1 for a list, 2+ for a grid).
 * @param title         Header title text.
 * @param isEditMode    Whether edit mode is currently active.
 * @param hasSelection  Whether any items are selected (controls Delete enabled state).
 * @param onToggleEditMode  Called when the Edit/Done button is tapped.
 * @param onAdd         Called when the Add button is tapped (edit mode only).
 * @param onDeleteSelected  Called when the Delete button is tapped (edit mode only).
 * @param onMove        Called with (fromIndex, toIndex) when an item is dragged.
 * @param onClearSelection  Called to clear selection when exiting edit mode.
 * @param itemContent   Composable slot for each item. Receives the item and a
 *                      [Modifier] for drag-handle behaviour.
 * @param modifier      Modifier applied to the outer grid.
 */
@Composable
fun <T> EditableItemGrid(
    items: List<T>,
    itemKey: (T) -> Any,
    columns: Int,
    title: String,
    isEditMode: Boolean,
    hasSelection: Boolean,
    onToggleEditMode: () -> Unit,
    onAdd: () -> Unit,
    onDeleteSelected: () -> Unit,
    onMove: (Int, Int) -> Unit,
    onClearSelection: () -> Unit,
    itemContent: @Composable (item: T, dragModifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    // Header row
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isEditMode) {
                // Add Button
                TextButton(
                    onClick = onAdd,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                // Delete Button
                TextButton(
                    onClick = onDeleteSelected,
                    enabled = hasSelection,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                ) {
                    Text(
                        text = "Delete",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            FilledTonalButton(
                onClick = {
                if (isEditMode) {
                    onClearSelection()
                }
                onToggleEditMode()
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Text(
                    text = if (isEditMode) "Done" else "Edit",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }

    // Grid / List
    val gridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(gridState) { from, to ->
        onMove(from.index, to.index)
    }

    // Auto-scroll to the last item when a new item is added
    var previousItemCount by remember { mutableIntStateOf(items.size) }
    LaunchedEffect(items.size) {
        if (items.size > previousItemCount && items.isNotEmpty()) {
            gridState.animateScrollToItem(items.size - 1)
        }
        previousItemCount = items.size
    }

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(items, key = itemKey) { item ->
            ReorderableItem(reorderableState, key = itemKey(item)) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)

                Box(modifier = Modifier.shadow(elevation)) {
                    itemContent(
                        item,
                        if (isEditMode) Modifier.draggableHandle() else Modifier
                    )
                }
            }
        }
    }
}
