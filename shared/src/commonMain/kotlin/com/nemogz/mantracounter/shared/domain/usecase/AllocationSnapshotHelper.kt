package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseAllocationDetailsEntity
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivity
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.model.Counter
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Reason for a mantra count change, determines which bucket to update.
 */
enum class MantraChangeReason {
    RECITED,      // user increment or positive set
    HOMEWORK,     // homework deduction
    LITTLE_HOUSE  // little house conversion deduction
}

/**
 * Updates a MantraAndHomeworkDetailsEntity for a given mantra change.
 *
 * @param activity The current DailyActivity domain model.
 * @param counter The counter being modified.
 * @param oldCount The mantra count before this change (only used for delta check).
 * @param newCount The mantra count after this change.
 * @return A new DailyActivity with the updated mantra details list.
 */
@OptIn(ExperimentalUuidApi::class)
internal fun updateMantraRecitedForCountChange(
    activity: DailyActivity,
    counter: Counter,
    oldCount: Int,
    newCount: Int,
    homeworkGoal: Int
): DailyActivity {
    val delta = newCount - oldCount
    if (delta == 0) return activity

    val mantras = activity.mantras.toMutableList()
    val existingIndex = mantras.indexOfFirst { it.mantraId == counter.id }

    if (existingIndex != -1) {
        val existing = mantras[existingIndex]
        mantras[existingIndex] = existing.copy(endCount = newCount, homeworkGoal = homeworkGoal)
    } else {
        // This handles counters created TODAY after the DayRollover occurred.
        // We initialize the startCount to the OLD count.
        val newEntry = MantraAndHomeworkDetailsEntity(
            key = Uuid.random().toString(),
            dailyActivityDate = activity.activity.date,
            mantraId = counter.id,
            mantraName = counter.name,
            mantraSortOrder = counter.sortOrder,
            startCount = oldCount,
            endCount = newCount,
            homeworkGoal = homeworkGoal
        )
        mantras.add(newEntry)
    }

    return activity.copy(mantras = mantras)
}

/**
 * Updates a LittleHouseAllocationDetailsEntity for a given recipient allocation or burn.
 *
 * @param activity The current DailyActivity domain model.
 * @param recipient The recipient being modified.
 * @param newBurnedCount The recipient's new global burned count after the action.
 * @return A new DailyActivity with the updated allocation details list.
 */
@OptIn(ExperimentalUuidApi::class)
internal fun updateAllocationForRecipient(
    activity: DailyActivity,
    recipient: LittleHouseRecipient,
    newBurnedCount: Int
): DailyActivity {
    val allocations = activity.allocations.toMutableList()
    val existingIndex = allocations.indexOfFirst { it.recipientId == recipient.id }

    if (existingIndex != -1) {
        val existing = allocations[existingIndex]
        allocations[existingIndex] = existing.copy(
            endCount = newBurnedCount,
            recipientSortOrder = recipient.sortOrder,
            recipientTargetFinishDate = recipient.targetFinishDate,
            allocationGoal = recipient.goal
        )
    } else {
        // This handles recipients created TODAY after the DayRollover occurred.
        // We initialize the startCount at whatever their burnedCount was prior to this change.
        // Assuming we pass in the newBurnedCount, we can infer startCount = newBurnedCount - delta
        // But since we don't have delta for recipients yet, wait...
        // The burned count only changes when they burn or allocate.
        // If they allocate, their burned count doesn't change.
        // Let's just pass `recipient.burnedCount` as `startCount` because they were just created.
        val newEntry = LittleHouseAllocationDetailsEntity(
            key = Uuid.random().toString(),
            dailyActivityDate = activity.activity.date,
            recipientId = recipient.id,
            recipientName = recipient.name,
            recipientSortOrder = recipient.sortOrder,
            recipientTargetFinishDate = recipient.targetFinishDate,
            startCount = recipient.burnedCount, // This is an approximation since they could be allocating
            endCount = newBurnedCount,
            allocationGoal = recipient.goal
        )
        allocations.add(newEntry)
    }

    return activity.copy(allocations = allocations)
}

