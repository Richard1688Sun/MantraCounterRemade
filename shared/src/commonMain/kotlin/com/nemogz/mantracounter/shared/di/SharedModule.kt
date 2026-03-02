package com.nemogz.mantracounter.shared.di

import com.nemogz.mantracounter.shared.data.local.AppDatabase
import com.nemogz.mantracounter.shared.data.repository.CounterRepositoryImpl
import com.nemogz.mantracounter.shared.data.repository.LittleHouseRepositoryImpl
import com.nemogz.mantracounter.shared.data.repository.LittleHouseRecipientRepositoryImpl
import com.nemogz.mantracounter.shared.domain.repository.ICounterRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRepository
import com.nemogz.mantracounter.shared.domain.repository.ILittleHouseRecipientRepository
import com.nemogz.mantracounter.shared.domain.repository.ISettingsRepository
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
import com.nemogz.mantracounter.shared.domain.usecase.SetCounterCountUseCase
import com.nemogz.mantracounter.shared.domain.repository.IDailyActivityRepository
import com.nemogz.mantracounter.shared.data.repository.DailyActivityRepositoryImpl
import com.nemogz.mantracounter.shared.domain.usecase.GetMissedHomeworkDaysUseCase

import com.nemogz.mantracounter.shared.domain.usecase.AllocateLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.CheckDayRolloverUseCase
import com.nemogz.mantracounter.shared.domain.usecase.CreateCounterUseCase
import com.nemogz.mantracounter.shared.domain.usecase.CreateLittleHouseRecipientUseCase
import com.nemogz.mantracounter.shared.domain.usecase.DeleteCountersUseCase
import com.nemogz.mantracounter.shared.domain.usecase.DeleteLittleHouseRecipientUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetActivitiesForMonthUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseNameUseCase
import com.nemogz.mantracounter.shared.domain.usecase.GetLittleHouseRecipientsUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UnallocateLittleHouseUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateLittleHouseRecipientUseCase
import com.nemogz.mantracounter.shared.domain.usecase.UpdateLittleHouseUseCase
import org.koin.dsl.module

val featureModule = module {
    // 1. DAOs - We assume AppDatabase is provided by the Platform Module
    single { get<AppDatabase>().counterDao() }
    single { get<AppDatabase>().littleHouseDao() }
    single { get<AppDatabase>().dailyActivityDao() }
    single { get<AppDatabase>().littleHouseRecipientDao() }
    single { get<AppDatabase>().littleHouseAllocationDetailsDao() }
    single { get<AppDatabase>().mantraAndHomeworkDetailsDao() }
    single { get<AppDatabase>().appSettingsDao() }

    // 2. Repositories
    single<ICounterRepository> { CounterRepositoryImpl(get()) }
    single<ISettingsRepository> { com.nemogz.mantracounter.shared.data.repository.SettingsRepositoryImpl(get()) }
    single<ILittleHouseRepository> { LittleHouseRepositoryImpl(get()) }
    single<IDailyActivityRepository> { DailyActivityRepositoryImpl(get(), get(), get()) }
    single<ILittleHouseRecipientRepository> { LittleHouseRecipientRepositoryImpl(get()) }

    // 3. Use Cases
    factory { IncrementCounterUseCase(get(), get()) }
    factory { GetCountersUseCase(get()) }
    factory { GetLittleHouseCountUseCase(get()) }
    factory { ConvertLittleHouseUseCase(get(), get(), get()) }
    factory { GetMissedHomeworkDaysUseCase(get()) }

    factory { CheckDayRolloverUseCase(get(), get(), get(), get()) }
    factory { CompleteHomeworkUseCase(get(), get()) }
    factory { GetCounterByIdUseCase(get()) }
    factory { UpdateHomeworkAmountUseCase(get()) }
    factory { UpdateCountersUseCase(get(), get()) }
    factory { UpdateCounterUseCase(get(), get()) }
    factory { CreateCounterUseCase(get(), get()) }
    factory { DeleteCountersUseCase(get()) }
    factory { ValidateCounterCountUseCase() }
    factory { SetCounterCountUseCase(get(), get()) }
    factory { GetActivitiesForMonthUseCase(get()) }
    factory { UpdateLittleHouseUseCase(get(), get()) }
    factory { GetLittleHouseNameUseCase(get()) }

    // Little House Recipient Use Cases
    factory { GetLittleHouseRecipientsUseCase(get()) }
    factory { CreateLittleHouseRecipientUseCase(get(), get()) }
    factory { UpdateLittleHouseRecipientUseCase(get(), get()) }
    factory { DeleteLittleHouseRecipientUseCase(get()) }
    factory { AllocateLittleHouseUseCase(get(), get(), get()) }
    factory { UnallocateLittleHouseUseCase(get(), get(), get()) }
}
