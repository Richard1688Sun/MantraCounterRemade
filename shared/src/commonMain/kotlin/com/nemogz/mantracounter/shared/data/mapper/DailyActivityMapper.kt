package com.nemogz.mantracounter.shared.data.mapper

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseAllocationDetailsEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseRecipientEntity
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity
import com.nemogz.mantracounter.shared.domain.model.DailyActivitySummary
import com.nemogz.mantracounter.shared.domain.model.LittleHouseAllocationDetails
import com.nemogz.mantracounter.shared.domain.model.LittleHouseRecipient
import com.nemogz.mantracounter.shared.domain.model.MantraAndHomeworkDetails

fun DailyActivityEntity.toDomain(): DailyActivitySummary {
    return DailyActivitySummary(
        date = date,
        homeworkCompletedDate = homeworkCompletedDate,
        littleHouseStartCount = littleHouseStartCount,
        littleHousesConverted = littleHousesConverted,
        littleHouseManualIncrease = littleHouseManualIncrease
    )
}

fun DailyActivitySummary.toEntity(): DailyActivityEntity {
    return DailyActivityEntity(
        date = date,
        homeworkCompletedDate = homeworkCompletedDate,
        littleHouseStartCount = littleHouseStartCount,
        littleHousesConverted = littleHousesConverted,
        littleHouseManualIncrease = littleHouseManualIncrease
    )
}

fun LittleHouseAllocationDetailsEntity.toDomain(): LittleHouseAllocationDetails {
    return LittleHouseAllocationDetails(
        key = key,
        dailyActivityDate = dailyActivityDate,
        recipientId = recipientId,
        recipientName = recipientName,
        recipientSortOrder = recipientSortOrder,
        recipientTargetFinishDate = recipientTargetFinishDate,
        startCount = startCount,
        endCount = endCount,
        allocationGoal = allocationGoal
    )
}

fun LittleHouseAllocationDetails.toEntity(): LittleHouseAllocationDetailsEntity {
    return LittleHouseAllocationDetailsEntity(
        key = key,
        dailyActivityDate = dailyActivityDate,
        recipientId = recipientId,
        recipientName = recipientName,
        recipientSortOrder = recipientSortOrder,
        recipientTargetFinishDate = recipientTargetFinishDate,
        startCount = startCount,
        endCount = endCount,
        allocationGoal = allocationGoal
    )
}

fun MantraAndHomeworkDetailsEntity.toDomain(): MantraAndHomeworkDetails {
    return MantraAndHomeworkDetails(
        key = key,
        dailyActivityDate = dailyActivityDate,
        mantraId = mantraId,
        mantraName = mantraName,
        mantraSortOrder = mantraSortOrder,
        startCount = startCount,
        endCount = endCount,
        homeworkGoal = homeworkGoal
    )
}

fun MantraAndHomeworkDetails.toEntity(): MantraAndHomeworkDetailsEntity {
    return MantraAndHomeworkDetailsEntity(
        key = key,
        dailyActivityDate = dailyActivityDate,
        mantraId = mantraId,
        mantraName = mantraName,
        mantraSortOrder = mantraSortOrder,
        startCount = startCount,
        endCount = endCount,
        homeworkGoal = homeworkGoal
    )
}

fun LittleHouseRecipientEntity.toDomain(): LittleHouseRecipient {
    return LittleHouseRecipient(
        id = id,
        name = name,
        goal = goal,
        targetFinishDate = targetFinishDate,
        burnedCount = burnedCount,
        sortOrder = sortOrder
    )
}

fun LittleHouseRecipient.toEntity(): LittleHouseRecipientEntity {
    return LittleHouseRecipientEntity(
        id = id,
        name = name,
        goal = goal,
        targetFinishDate = targetFinishDate,
        burnedCount = burnedCount,
        sortOrder = sortOrder
    )
}
