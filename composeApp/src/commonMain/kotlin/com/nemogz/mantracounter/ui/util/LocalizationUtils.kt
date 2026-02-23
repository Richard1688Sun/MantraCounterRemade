package com.nemogz.mantracounter.ui.util

import androidx.compose.runtime.Composable
import mantracounterremade.composeapp.generated.resources.Res
import mantracounterremade.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun getLocalizedMantraName(name: String): String = when (name) {
    "Great Compassion Mantra" -> stringResource(Res.string.mantra_dabei)
    "Heart Sutra" -> stringResource(Res.string.mantra_boruo)
    "Amitabha Pure Land Rebirth Mantra" -> stringResource(Res.string.mantra_wangshen)
    "Sapta Atita Tathagata Mantra" -> stringResource(Res.string.mantra_qifo)
    "Guan Yin Bodhisattva's Mantra" -> stringResource(Res.string.mantra_dabei) // Fallback or specific? I'll stick to what's in resources.
    "Self" -> stringResource(Res.string.recipient_self)
    else -> name
}

@Composable
fun formatFullDate(date: kotlinx.datetime.LocalDate): String {
    val monthRes = when (date.monthNumber) {
        1 -> Res.string.month_1
        2 -> Res.string.month_2
        3 -> Res.string.month_3
        4 -> Res.string.month_4
        5 -> Res.string.month_5
        6 -> Res.string.month_6
        7 -> Res.string.month_7
        8 -> Res.string.month_8
        9 -> Res.string.month_9
        10 -> Res.string.month_10
        11 -> Res.string.month_11
        12 -> Res.string.month_12
        else -> Res.string.month_1
    }
    return stringResource(Res.string.date_pattern_full, stringResource(monthRes), date.dayOfMonth, date.year)
}

@Composable
fun getLocalizedMonthName(monthNumber: Int): String {
    val monthRes = when (monthNumber) {
        1 -> Res.string.month_1
        2 -> Res.string.month_2
        3 -> Res.string.month_3
        4 -> Res.string.month_4
        5 -> Res.string.month_5
        6 -> Res.string.month_6
        7 -> Res.string.month_7
        8 -> Res.string.month_8
        9 -> Res.string.month_9
        10 -> Res.string.month_10
        11 -> Res.string.month_11
        12 -> Res.string.month_12
        else -> Res.string.month_1
    }
    return stringResource(monthRes)
}
