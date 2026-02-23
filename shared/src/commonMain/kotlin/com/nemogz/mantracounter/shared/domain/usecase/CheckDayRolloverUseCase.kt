package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseAllocationDetailsEntity
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CheckDayRolloverUseCase(
    private val dailyActivityRepository: IDailyActivityRepository,
    private val counterRepository: ICounterRepository,
    private val recipientRepository: ILittleHouseRecipientRepository
) {
    /**
     * Ensures that DailyActivity rows exist from the most recent
     * recorded date up to today. Since DatabaseSeeder already guarantees
     * all days from first to most-recent are filled, we only need to
     * fill the gap from mostRecent+1 to today.
     * When creating a new day, we snapshot the current counts to initialize startCount.
     */
    suspend operator fun invoke() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val mostRecent = dailyActivityRepository.getMostRecentActivityDate()

        if (mostRecent == null) {
            // No records at all — just create today with current snapshot
            createNewDay(today)
        } else if (mostRecent < today) {
            // Fill from mostRecent+1 to today
            // For intermediate missed days, we also use the current snapshot,
            // assuming no activity happened on those days, so the count remained the same.
            for (date in (mostRecent + 1)..today) {
                createNewDay(date)
            }
        }
        // If mostRecent == today, nothing to do
    }

    private suspend fun createNewDay(date: Long) {
        val counters = counterRepository.getAllCounters().first()
        val recipients = recipientRepository.getAll().first()

        val initialMantras = counters.map {
            MantraAndHomeworkDetailsEntity(
                key = MantraAndHomeworkDetailsEntity.generateKey(date, it.id),
                dailyActivityDate = date,
                mantraId = it.id,
                mantraName = it.name,
                mantraSortOrder = it.sortOrder,
                startCount = it.count,
                endCount = it.count,
                homeworkGoal = it.homeworkGoal
            )
        }

        val initialAllocations = recipients.map {
            LittleHouseAllocationDetailsEntity(
                key = LittleHouseAllocationDetailsEntity.generateKey(date, it.id),
                dailyActivityDate = date,
                recipientId = it.id,
                recipientName = it.name,
                recipientSortOrder = it.sortOrder,
                recipientTargetFinishDate = it.targetFinishDate,
                startCount = it.burnedCount,
                endCount = it.burnedCount,
                allocationGoal = it.goal
            )
        }

        val emptyActivity = DailyActivity(
            activity = DailyActivityEntity(date = date),
            allocations = initialAllocations,
            mantras = initialMantras
        )

        dailyActivityRepository.insertOrUpdateActivity(emptyActivity)
    }
}
