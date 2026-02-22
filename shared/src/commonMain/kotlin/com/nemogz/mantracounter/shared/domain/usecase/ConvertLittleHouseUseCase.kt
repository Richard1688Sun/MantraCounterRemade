package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ConvertLittleHouseUseCase(
    private val counterRepository: ICounterRepository,
    private val littleHouseRepository: ILittleHouseRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {
    /**
     * Attempts to convert counts into LittleHouses.
     * @return The number of LittleHouses created (0 if not enough counts).
     */
    suspend operator fun invoke(): Int {
        val counters = counterRepository.getAllCounters().first()
        
        // 1. Identify the core counters
        val dabei = counters.find { it.mantraType == MantraType.DaBei }
        val boruo = counters.find { it.mantraType == MantraType.BoRuo }
        val wangshen = counters.find { it.mantraType == MantraType.WangShen }
        val qifo = counters.find { it.mantraType == MantraType.QiFo }

        if (dabei == null || boruo == null || wangshen == null || qifo == null) {
            return 0
        }

        // 2. Calculate max possible sets
        val setsDabei = dabei.count / MantraType.DaBei.mantraGoalCount
        val setsBoruo = boruo.count / MantraType.BoRuo.mantraGoalCount
        val setsWangshen = wangshen.count / MantraType.WangShen.mantraGoalCount
        val setsQifo = qifo.count / MantraType.QiFo.mantraGoalCount

        val minimalSets = minOf(setsDabei, setsBoruo, setsWangshen, setsQifo)

        // Only convert 1 little house at a time
        val setsToConvert = if (minimalSets > 0) 1 else 0

        if (setsToConvert > 0) {
            // 3. Deduct counts
            val newDabeiCount = dabei.count - (setsToConvert * MantraType.DaBei.mantraGoalCount)
            val newBoruoCount = boruo.count - (setsToConvert * MantraType.BoRuo.mantraGoalCount)
            val newWangshenCount = wangshen.count - (setsToConvert * MantraType.WangShen.mantraGoalCount)
            val newQifoCount = qifo.count - (setsToConvert * MantraType.QiFo.mantraGoalCount)

            // 4. Update Repositories
            counterRepository.updateCounts(
                ids = listOf(dabei.id, boruo.id, wangshen.id, qifo.id),
                newCounts = listOf(newDabeiCount, newBoruoCount, newWangshenCount, newQifoCount)
            )

            littleHouseRepository.incrementLittleHouseCount(setsToConvert)

            // Log conversion in daily activity
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
            val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: DailyActivity(DailyActivityEntity(date = today), emptyList(), emptyList())

            // Log little house deductions
            var updatedActivity = activity
            val deductions = listOf(
                dabei to newDabeiCount,
                boruo to newBoruoCount,
                wangshen to newWangshenCount,
                qifo to newQifoCount
            )
            for ((counter, newCount) in deductions) {
                updatedActivity = updateMantraRecitedForCountChange(
                    updatedActivity, counter.name, counter.count, newCount, counter.homeworkGoal
                )
            }

            dailyActivityRepository.insertOrUpdateActivity(
                updatedActivity.copy(
                    activity = updatedActivity.activity.copy(
                        littleHousesConverted = updatedActivity.activity.littleHousesConverted + setsToConvert
                    )
                )
            )
        }

        return setsToConvert
    }
}
