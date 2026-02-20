package com.nemogz.mantracounter.shared.data.local

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import com.nemogz.mantracounter.shared.domain.usecase.CheckDayRolloverUseCase
import kotlinx.coroutines.flow.first

class DatabaseSeeder(
    private val counterRepository: ICounterRepository,
    private val littleHouseRepository: ILittleHouseRepository,
    private val checkDayRolloverUseCase: CheckDayRolloverUseCase,
    private val recipientRepository: ILittleHouseRecipientRepository
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
                    name = "Great Compassion Mantra",
                    sortOrder = 0
                ),
                Counter(
                    id = "boruo",
                    mantraType = MantraType.BoRuo,
                    name = "Heart Sutra",
                    sortOrder = 1
                ),
                Counter(
                    id = "wangshen",
                    mantraType = MantraType.WangShen,
                    name = "Amitabha Pure Land Rebirth Mantra",
                    sortOrder = 2
                ),
                Counter(
                    id = "qifo",
                    mantraType = MantraType.QiFo,
                    name = "Sapta Atita Tathagata Mantra",
                    sortOrder = 3
                )
            )

            defaults.forEach { counterRepository.saveCounter(it) }
        }

        // 3. Ensure LittleHouse record exists
        val lhCount = littleHouseRepository.getLittleHouseCount().first()
        if (currentCounters.isEmpty()) {
             littleHouseRepository.setLittleHouseCount(0)
        }

        // 4. Ensure default "Self" recipient exists
        val recipientCount = recipientRepository.getCount()
        if (recipientCount == 0) {
            recipientRepository.insert(
                LittleHouseRecipient(
                    id = LittleHouseRecipient.DEFAULT_SELF_ID,
                    name = "Self",
                    goal = 0,
                    sortOrder = 0
                )
            )
        }

        // 5. Backfill Daily Activity entries (reuses CheckDayRolloverUseCase)
        checkDayRolloverUseCase()
    }
}
