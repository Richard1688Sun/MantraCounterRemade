package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.first

class ConvertLittleHouseUseCase(
    private val counterRepository: ICounterRepository,
    private val littleHouseRepository: ILittleHouseRepository
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

            if (minimalSets > 0) {
                // 3. Deduct counts
                val newDabeiCount = dabei.count - (minimalSets * MantraType.DaBei.mantraGoalCount)
                val newBoruoCount = boruo.count - (minimalSets * MantraType.BoRuo.mantraGoalCount)
                val newWangshenCount = wangshen.count - (minimalSets * MantraType.WangShen.mantraGoalCount)
                val newQifoCount = qifo.count - (minimalSets * MantraType.QiFo.mantraGoalCount)

            // 4. Update Repositories
            // We update counters individually or in batch if repo supports it.
            // Using batch update helper would be better, but for now individual saves are fine or we add batch method.
            // I added updateCounts to ICounterRepository earlier!
            
            counterRepository.updateCounts(
                ids = listOf(dabei.id, boruo.id, wangshen.id, qifo.id),
                newCounts = listOf(newDabeiCount, newBoruoCount, newWangshenCount, newQifoCount)
            )

            littleHouseRepository.incrementLittleHouseCount(minimalSets)
        }

        return minimalSets
    }
}
