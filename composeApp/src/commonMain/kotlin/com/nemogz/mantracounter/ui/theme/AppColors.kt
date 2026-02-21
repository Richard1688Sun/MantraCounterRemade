package com.nemogz.mantracounter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Semantic app colours derived from the current [MaterialTheme] colour scheme.
 * Access via [MaterialTheme.appColors].
 */
@Immutable
data class AppColors(
    /** Green dot — homework completed for the day. */
    val homeworkCompleted: Color,
    /** Grey dot — homework pending / not completed. */
    val homeworkNotCompleted: Color,
    /** Golden colour — little houses converted. */
    val converted: Color,
    /** Primary colour — little houses burned (offered). */
    val burned: Color,
    /** Positive addition — mantra recited breakdown. */
    val recited: Color,
    /** Deduction colour — homework deduction in breakdown. */
    val homework: Color,
    /** Deduction colour — little house deduction in breakdown. */
    val littleHouse: Color,
)

/** Composable extension to retrieve [AppColors] from the current [MaterialTheme]. */
val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = AppColors(
        homeworkCompleted    = colorScheme.tertiary,           // lime-green = positive/complete
        homeworkNotCompleted = colorScheme.outline,            // neutral grey-ish label
        converted            = colorScheme.secondary,          // golden-yellow secondary
        burned               = colorScheme.primary,            // deep gold primary
        recited              = colorScheme.tertiary,           // positive addition
        homework             = colorScheme.error,              // red deduction
        littleHouse          = colorScheme.error,              // red deduction
    )
