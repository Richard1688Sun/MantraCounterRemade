package com.nemogz.mantracounter.di

import com.nemogz.mantracounter.shared.data.local.DatabaseSeeder
import com.nemogz.mantracounter.ui.home.HomeViewModel
import com.nemogz.mantracounter.ui.detail.CounterDetailViewModel
import com.nemogz.mantracounter.shared.domain.usecase.CompleteHomeworkUseCase
import com.nemogz.mantracounter.shared.domain.usecase.ConvertLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseCountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.IncrementCounterUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCounterUseCase
import org.koin.dsl.module

val viewModelModule = module {
    factory { 
        HomeViewModel(
            getCountersUseCase = get(), 
            getLittleHouseCountUseCase = get(), 
            incrementCounterUseCase = get(), 
            convertLittleHouseUseCase = get(), 
            burnLittleHouseUseCase = get(),
            getMissedHomeworkDaysUseCase = get(),
            completeHomeworkUseCase = get(), 
            catchUpHomeworkUseCase = get(),
            updateCountersUseCase = get(), 
            updateCounterUseCase = get(),
            createCounterUseCase = get(),
            deleteCountersUseCase = get(),
            validateCounterCountUseCase = get(),
            checkDayRolloverUseCase = get()
        ) 
    }
    factory {
        com.nemogz.mantracounter.ui.homework.HomeworkViewModel(
            getCountersUseCase = get(),
            updateCounterUseCase = get(),
            getMissedHomeworkDaysUseCase = get(),
            catchUpHomeworkUseCase = get()
        )
    }
    factory { CounterDetailViewModel(get(), get(), get(), get()) }
    single { DatabaseSeeder(get(), get(), get()) }
}
