package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CheckDayRolloverUseCase(
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Ensures that DailyActivityEntity rows exist from the most recent
     * recorded date up to today. Since DatabaseSeeder already guarantees
     * all days from first to most-recent are filled, we only need to
     * fill the gap from mostRecent+1 to today.
     */
    suspend operator fun invoke() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val mostRecent = dailyActivityRepository.getMostRecentActivityDate()

        if (mostRecent == null) {
            // No records at all — just create today
            dailyActivityRepository.insertOrUpdateActivity(DailyActivityEntity(date = today))
        } else if (mostRecent < today) {
            // Fill from mostRecent+1 to today
            for (date in (mostRecent + 1)..today) {
                dailyActivityRepository.insertOrUpdateActivity(DailyActivityEntity(date = date))
            }
        }
        // If mostRecent == today, nothing to do
    }
}
