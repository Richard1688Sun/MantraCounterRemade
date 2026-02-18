package com.nemogz.mantracounter.shared.data.local

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import kotlinx.coroutines.flow.first

class DatabaseSeeder(
    private val counterRepository: ICounterRepository,
    private val littleHouseRepository: ILittleHouseRepository
) {
    suspend fun seed() {
        // 1. Check if counters exist
        val currentCounters = counterRepository.getAllCounters().first()
        
        if (currentCounters.isEmpty()) {
            // 2. Create Default Counters
            val defaults = listOf(
                Counter(
                    id = "dabei",
                    mantraType = MantraType.DaBei,
                    name = "Great Compassion Mantra" // Or use localized string resource if possible? For now hardcode or use simple names
                    // Actually legacy uses "dabei" as original name. Let's use nicer names or match legacy?
                    // Legacy: context.getString(R.string.dabei) -> likely "Da Bei Zhou" or Chinese.
                    // For this migration, I will use English/Pinyin names or meaningful defaults.
                ),
                Counter(
                    id = "boruo",
                    mantraType = MantraType.BoRuo,
                    name = "Heart Sutra"
                ),
                Counter(
                    id = "wangshen",
                    mantraType = MantraType.WangShen,
                    name = "Amitabha Pure Land Rebirth Mantra"
                ),
                Counter(
                    id = "qifo",
                    mantraType = MantraType.QiFo,
                    name = "Sapta Atituatha Tathagata Mantra"
                )
            )

            defaults.forEach { counterRepository.saveCounter(it) }
        }

        // 3. Ensure LittleHouse record exists
        val lhCount = littleHouseRepository.getLittleHouseCount().first()
        // If flow returns 0 (mapped from null), we are good. 
        // But we want to ensure the row exists so updates work.
        // My repo implementation: `getLittleHouseCount` returns 0 if null.
        // `setLittleHouseCount` inserts/replaces.
        // `incrementLittleHouseCount` updates. 
        // If row doesn't exist, `increment` might fail depending on SQL.
        // Safe bet: set it to 0 explicitly if we just seeded counters.
        if (currentCounters.isEmpty()) {
             littleHouseRepository.setLittleHouseCount(0)
        }
    }
}
