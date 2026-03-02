package com.nemogz.mantracounter.shared.domain.model

data class DailyActivity(
    val activity: DailyActivitySummary,
    val allocations: List<LittleHouseAllocationDetails>,
    val mantras: List<MantraAndHomeworkDetails>
)

data class DailyActivitySummary(
    val date: Long, // Epoch day
    val homeworkCompletedDate: Long? = null,
    val littleHouseStartCount: Int = 0,
    val littleHousesConverted: Int = 0,
    val littleHouseManualIncrease: Int = 0
)

data class LittleHouseAllocationDetails(
    val key: String,
    val dailyActivityDate: Long,
    val recipientId: String,
    val recipientName: String,
    val recipientSortOrder: Int,
    val recipientTargetFinishDate: Long?,
    val startCount: Int,
    val endCount: Int,
    val allocationGoal: Int
) {
    companion object {
        fun generateKey(date: Long, recipientId: String): String = "alloc_${date}_$recipientId"
    }
}

data class MantraAndHomeworkDetails(
    val key: String,
    val dailyActivityDate: Long,
    val mantraId: String,
    val mantraName: String,
    val mantraSortOrder: Int,
    val startCount: Int,
    val endCount: Int,
    val homeworkGoal: Int
) {
    companion object {
        fun generateKey(date: Long, mantraId: String): String = "mantra_${date}_$mantraId"
    }
}
