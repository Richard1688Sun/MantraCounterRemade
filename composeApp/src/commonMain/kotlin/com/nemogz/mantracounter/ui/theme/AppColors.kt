package com.nemogz.mantracounter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

@Immutable
data class CustomColors(
    val lotus: Color,
)

val LightCustomColors = CustomColors(
    lotus = Color(0xFFEE6DAA),
)

val DarkCustomColors = CustomColors(
    lotus = Color(0xFFEE6DAA), // Same color for dark mode unless specified
)

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }

/**
 * Semantic app colours derived from the current [MaterialTheme] colour scheme.
 * Access via [MaterialTheme.appColors].
 */
@Immutable
data class AppColors(
    /** Green dot — homework completed for the day. */
    val homeworkCompletedDot: Color,
    /** Green background row — homework completed for the day. */
    val homeworkCompletedRow: Color,
    /** Grey dot — homework pending / not completed. */
    val homeworkNotCompletedDot: Color,
    /** Grey background row — homework pending / not completed. */
    val homeworkNotCompletedRow: Color,
    /** Golden colour — little houses converted. */
    val convertedHouseDot: Color,
    /** Golden background row — little houses converted. */
    val convertedHouseRow: Color,
    /** Primary colour — little houses burned (offered). */
    val burnedHouseDot: Color,
    /** Primary background row — little houses burned (offered). */
    val burnedHouseRow: Color,
    /** Positive addition — mantra recited breakdown. */
    val recitedMantraDot: Color,
    /** Positive addition row — mantra recited breakdown. */
    val recitedMantraRow: Color,
    /** Deduction colour — homework deduction in breakdown. */
    val homeworkDeductionDot: Color,
    /** Deduction background row — homework deduction in breakdown. */
    val homeworkDeductionRow: Color,
    /** Deduction colour — little house deduction in breakdown. */
    val littleHouseDeductionDot: Color,
    /** Deduction background row — little house deduction in breakdown. */
    val littleHouseDeductionRow: Color,
    /** Default color for incomplete progress in GoalProgressBar. */
    val progressBarIncomplete: Color,
    /** Default color for completed progress or today's new progress in GoalProgressBar. */
    val progressBarComplete: Color,
    /** Error color for failed operations. */
    val errorColor: Color,
)

/** Composable extension to retrieve [AppColors] from the current [MaterialTheme]. */
val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = AppColors(
        homeworkCompletedDot      = Color(0xFF4CAF50),             // lime-green = positive/complete
        homeworkCompletedRow      = Color(0xFFE8F5E9),    // lime-green background
        homeworkNotCompletedDot   = Color(0xFF9E9E9E),              // neutral grey-ish label
        homeworkNotCompletedRow   = Color(0xFFF5F5F5),       // neutral grey background
        convertedHouseDot         = Color(0xFFFFC107),            // golden-yellow secondary
        convertedHouseRow         = Color(0xFFFFF8E1),   // golden-yellow background
        burnedHouseDot            = Color(0xFFFF9800),              // deep gold primary
        burnedHouseRow            = Color(0xFFFFF3E0),     // deep gold background
        recitedMantraDot          = Color(0xFF4CAF50),             // positive addition
        recitedMantraRow          = Color(0xFFE8F5E9),    // positive addition background
        homeworkDeductionDot      = Color(0xFFF44336),                // red deduction
        homeworkDeductionRow      = Color(0xFFFFEBEE),       // red deduction background
        littleHouseDeductionDot   = Color(0xFFF44336),                // red deduction
        littleHouseDeductionRow   = Color(0xFFFFEBEE),       // red deduction background
        progressBarIncomplete     = Color(0xFF9E9E9E),       // generic grey for incomplete progress
        progressBarComplete       = Color(0xFF4CAF50),       // generic green for complete/today progress
        errorColor                = Color(0xFFCE2317),                // generic red for errors
    )
