package com.nemogz.mantracounter.shared.domain.usecase

import com.nemogz.mantracounter.shared.domain.model.Counter
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UpdateCounterUseCase(
    private val counterRepository: ICounterRepository,
    private val dailyActivityRepository: IDailyActivityRepository
) {

    suspend operator fun invoke(counter: Counter) {
        counterRepository.updateCounter(counter)
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toEpochDays().toLong()
        val activity = dailyActivityRepository.getDailyActivityByDate(today) ?: return
        
        val updatedMantras = activity.mantras.map { 
            if (it.mantraId == counter.id) {
                it.copy(
                    mantraName = counter.name,
                    mantraSortOrder = counter.sortOrder,
                    homeworkGoal = counter.homeworkGoal
                )
            } else {
                it
            }
        }
        dailyActivityRepository.insertOrUpdateActivity(
            activity.copy(mantras = updatedMantras)
        )
    }
}