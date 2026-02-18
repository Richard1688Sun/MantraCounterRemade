package com.nemogz.mantracounter.shared.domain.model

/**
 * Represents the specific type of Mantra and its rules.
 */
enum class MantraType(
    val id: String,
    val defaultTargetWait: Int,
    val isLittleHouseComponent: Boolean
) {
    DaBei("dabei", 27, true),
    BoRuo("boruo", 49, true),
    WangShen("wangshen", 84, true),
    QiFo("qifo", 87, true),
    Other("other", 0, false);

    companion object {
        fun getById(id: String): MantraType = entries.find { it.id == id } ?: Other
    }
}
