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
            getCountersUseCase = get<GetCountersUseCase>(), 
            getLittleHouseCountUseCase = get<GetLittleHouseCountUseCase>(), 
            incrementCounterUseCase = get<IncrementCounterUseCase>(), 
            convertLittleHouseUseCase = get<ConvertLittleHouseUseCase>(), 
            completeHomeworkUseCase = get<CompleteHomeworkUseCase>(), 
            updateCountersUseCase = get<UpdateCountersUseCase>(), 
            updateCounterUseCase = get<UpdateCounterUseCase>(),
            createCounterUseCase = get(),
            deleteCountersUseCase = get(),
            validateCounterCountUseCase = get()
        ) 
    }
    factory { CounterDetailViewModel(get(), get(), get(), get()) }
    single { DatabaseSeeder(get(), get()) }
}
