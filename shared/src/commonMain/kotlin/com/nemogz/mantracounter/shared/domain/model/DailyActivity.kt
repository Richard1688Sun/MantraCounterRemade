package com.nemogz.mantracounter.shared.domain.model

import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseAllocationDetailsEntity
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity

data class DailyActivity(
    val activity: DailyActivityEntity,
    val allocations: List<LittleHouseAllocationDetailsEntity>,
    val mantras: List<MantraAndHomeworkDetailsEntity>
)
