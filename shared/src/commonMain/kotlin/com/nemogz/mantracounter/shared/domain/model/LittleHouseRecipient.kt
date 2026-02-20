package com.nemogz.mantracounter.shared.domain.model

import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseRecipientEntity

data class LittleHouseRecipient(
    val id: String,
    val name: String,
    val goal: Int = 0,
    val targetFinishDate: Long? = null, // Epoch day
    val burnedCount: Int = 0,
    val sortOrder: Int = 0
) {
    val isDefault: Boolean get() = id == DEFAULT_SELF_ID
    val progress: Float get() = if (goal > 0) (burnedCount.toFloat() / goal).coerceAtMost(1f) else 0f
    val isGoalComplete: Boolean get() = goal > 0 && burnedCount >= goal

    companion object {
        const val DEFAULT_SELF_ID = "self"
    }
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

