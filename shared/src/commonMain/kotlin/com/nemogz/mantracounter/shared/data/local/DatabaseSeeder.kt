package com.nemogz.mantracounter.shared.data.local

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.model.MantraType
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import com.nemogz.mantracounter.shared.domain.usecase.CheckDayRolloverUseCase
import kotlinx.coroutines.flow.first

import mantracounterremade.shared.generated.resources.Res
import mantracounterremade.shared.generated.resources.*
import org.jetbrains.compose.resources.getString
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
                    name = getString(Res.string.mantra_dabei),
                    sortOrder = 0
                ),
                Counter(
                    id = "boruo",
                    mantraType = MantraType.BoRuo,
                    name = getString(Res.string.mantra_boruo),
                    sortOrder = 1
                ),
                Counter(
                    id = "wangshen",
                    mantraType = MantraType.WangShen,
                    name = getString(Res.string.mantra_wangshen),
                    sortOrder = 2
                ),
                Counter(
                    id = "qifo",
                    mantraType = MantraType.QiFo,
                    name = getString(Res.string.mantra_qifo),
                    sortOrder = 3
                )
            )

            defaults.forEach { counterRepository.insertCounter(it) }
        }

        // 3. Ensure LittleHouse record exists
        littleHouseRepository.insertInitialLittleHouseIfEmpty()

        // 4. Ensure default "Self" recipient exists
        val recipientCount = recipientRepository.getCount()
        if (recipientCount == 0) {
            recipientRepository.insertLittleHouseRecipient(
                LittleHouseRecipient(
                    id = LittleHouseRecipient.DEFAULT_SELF_ID,
                    name = getString(Res.string.recipient_self),
                    goal = 0,
                    sortOrder = 0
                )
            )
        }

        // 5. Backfill Daily Activity entries (reuses CheckDayRolloverUseCase)
        checkDayRolloverUseCase()
    }
}
