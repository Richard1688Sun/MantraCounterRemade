package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class BurnLittleHouseUseCase(
    private val littleHouseRepository: ILittleHouseRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Attempts to burn (use) a Little House.
     * @return true if successfully burned, false if not enough Little Houses.
     */
    suspend operator fun invoke(): Boolean {
        val currentCount = littleHouseRepository.getLittleHouseCount().first()
        if (currentCount > 0) {
            // Decrement little house count
            littleHouseRepository.incrementLittleHouseCount(-1)

            // Log burn in daily activity
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivityEntity(date = today)
            dailyActivityRepository.insertOrUpdateActivity(
                activity.copy(littleHousesBurned = activity.littleHousesBurned + 1)
            )
            return true
        }
        return false
    }
}
