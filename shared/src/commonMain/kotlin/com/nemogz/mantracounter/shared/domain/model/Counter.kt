package com.nemogz.mantracounter.shared.domain.model

/**
 * a pure domain entity representing a Counter.
 * @param id Unique identifier for the counter.
 * @param mantraType The type of mantra this counter tracks.
 * @param name Display name of the counter.
 * @param count Current count value.
 * @param homeworkAmount Daily homework requirement for this counter (default 0).
 */
data class Counter(
    val id: String,
    val mantraType: MantraType,
    val name: String,
    val count: Int = 0,
    val homeworkAmount: Int = 0
) {
    fun increment(): Counter = copy(count = count + 1)
    fun decrement(): Counter = if (count > 0) copy(count = count - 1) else this
    fun reset(): Counter = copy(count = 0)
    
    // Domain helper: Does this counter have enough for homework?
    fun hasEnoughForHomework(): Boolean = count >= homeworkAmount
}
