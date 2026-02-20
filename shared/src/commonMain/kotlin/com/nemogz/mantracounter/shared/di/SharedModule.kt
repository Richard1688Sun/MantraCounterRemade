package com.nemogz.mantracounter.shared.di

import com.nemogz.mantracounter.shared.data.local.AppDatabase
import com.nemogz.mantracounter.shared.data.repository.CounterRepositoryImpl
import com.nemogz.mantracounter.shared.data.repository.LittleHouseRepositoryImpl
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.usecase.CompleteHomeworkUseCase
import com.nemogz.mantracounter.shared.domain.usecase.ConvertLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseCountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetCounterByIdUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateCounterUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateHomeworkAmountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.ValidateCounterCountUseCase
import com.nemogz.mantracounter.shared.domain.usecase.IncrementCounterUseCase
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.data.repository.DailyActivityRepositoryImpl
import com.nemogz.mantracounter.shared.domain.usecase.BurnLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetMissedHomeworkDaysUseCase
import com.nemogz.mantracounter.shared.domain.usecase.CatchUpHomeworkUseCase
import org.koin.dsl.module

val featureModule = module {
    // 1. DAOs - We assume AppDatabase is provided by the Platform Module
    single { get<AppDatabase>().counterDao() }
    single { get<AppDatabase>().littleHouseDao() }
    single { get<AppDatabase>().dailyActivityDao() }

    // 2. Repositories
    single<ICounterRepository> { CounterRepositoryImpl(get()) }
    single<ILittleHouseRepository> { LittleHouseRepositoryImpl(get()) }
    single<IDailyActivityRepository> { DailyActivityRepositoryImpl(get()) }

    // 3. Use Cases
    factory { IncrementCounterUseCase(get()) }
    factory { GetCountersUseCase(get()) }
    factory { GetLittleHouseCountUseCase(get()) }
    factory { ConvertLittleHouseUseCase(get(), get(), get()) }
    factory { BurnLittleHouseUseCase(get(), get()) }
    factory { GetMissedHomeworkDaysUseCase(get()) }
    factory { CatchUpHomeworkUseCase(get()) }
    factory { com.nemogz.mantracounter.shared.domain.usecase.CheckDayRolloverUseCase(get()) }
    factory { CompleteHomeworkUseCase(get()) }
    factory { GetCounterByIdUseCase(get()) }
    factory { UpdateHomeworkAmountUseCase(get()) }
    factory { UpdateCountersUseCase(get()) }
    factory { UpdateCounterUseCase(get()) }
    factory { com.nemogz.mantracounter.shared.domain.usecase.CreateCounterUseCase(get()) }
    factory { com.nemogz.mantracounter.shared.domain.usecase.DeleteCountersUseCase(get()) }
    factory { ValidateCounterCountUseCase() }
    factory { com.nemogz.mantracounter.shared.domain.usecase.GetActivitiesForMonthUseCase(get()) }

}
