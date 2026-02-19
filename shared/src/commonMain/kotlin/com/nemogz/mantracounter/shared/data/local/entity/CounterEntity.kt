package com.nemogz.mantracounter.shared.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.model.MantraType

@Entity(tableName = "counters")
data class CounterEntity(
    @PrimaryKey val id: String,
    val mantraId: String, // Maps to MantraType.id
    val name: String,
    val count: Int,
    val homeworkGoal: Int,
    val sortOrder: Int
)

fun CounterEntity.toDomain(): Counter {
    return Counter(
        id = id,
        mantraType = MantraType.getById(mantraId),
        name = name,
        count = count,
        homeworkGoal = homeworkGoal,
        sortOrder = sortOrder
    )
}

fun Counter.toEntity(): CounterEntity {
    return CounterEntity(
        id = id,
        mantraId = mantraType.id,
        name = name,
        count = count,
        homeworkGoal = homeworkGoal,
        sortOrder = sortOrder
    )
}
