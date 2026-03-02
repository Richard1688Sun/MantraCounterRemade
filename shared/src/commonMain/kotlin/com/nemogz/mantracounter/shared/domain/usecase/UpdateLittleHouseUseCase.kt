package com.nemogz.mantracounter.shared.domain.usecase


import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.util.platformLog
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UpdateLittleHouseUseCase(
    private val littleHouseRepository: ILittleHouseRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Updates the Little House name and count.
     * If the count changed, updates the amount converted today by adding the difference, 
     * tracked purely for calendar reporting.
     */
    suspend operator fun invoke(newName: String, newCount: Int) {
        val oldCount = littleHouseRepository.getLittleHouseCount().first()

        littleHouseRepository.setLittleHouseName(newName)
        littleHouseRepository.setLittleHouseCount(newCount)

        if (oldCount != newCount) {
            val netChange = newCount - oldCount
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            val activity = dailyActivityRepository.getDailyActivityByDate(today)
            if (activity == null) {
                platformLog("DailyActivity", "ERROR: DailyActivity missing for today in UpdateLittleHouseUseCase")
                return
            }
            
            val currentManualIncrease = activity.activity.littleHouseManualIncrease
            val newManualIncrease = currentManualIncrease + netChange

            dailyActivityRepository.updateActivity(
                activity.copy(
                    activity = activity.activity.copy(littleHouseManualIncrease = newManualIncrease)
                )
            )
        }
    }
}
