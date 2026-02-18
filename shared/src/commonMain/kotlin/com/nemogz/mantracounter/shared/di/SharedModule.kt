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
import org.koin.dsl.module

val featureModule = module {
    // 1. DAOs - We assume AppDatabase is provided by the Platform Module
    single { get<AppDatabase>().counterDao() }
    single { get<AppDatabase>().littleHouseDao() }

    // 2. Repositories
    single<ICounterRepository> { CounterRepositoryImpl(get()) }
    single<ILittleHouseRepository> { LittleHouseRepositoryImpl(get()) }

    // 3. Use Cases
    factory { IncrementCounterUseCase(get()) }
    factory { GetCountersUseCase(get()) }
    factory { GetLittleHouseCountUseCase(get()) }
    factory { ConvertLittleHouseUseCase(get(), get()) }
    factory { CompleteHomeworkUseCase(get()) }
    factory { GetCounterByIdUseCase(get()) }
    factory { UpdateHomeworkAmountUseCase(get()) }
    factory { UpdateCountersUseCase(get()) }
    factory { UpdateCounterUseCase(get()) }
    factory { com.nemogz.mantracounter.shared.domain.usecase.CreateCounterUseCase(get()) }
    factory { com.nemogz.mantracounter.shared.domain.usecase.DeleteCountersUseCase(get()) }
    factory { ValidateCounterCountUseCase() }

}
