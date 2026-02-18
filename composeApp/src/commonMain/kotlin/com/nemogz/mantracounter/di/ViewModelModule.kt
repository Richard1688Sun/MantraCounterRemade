package com.nemogz.mantracounter.di

import com.nemogz.mantracounter.shared.data.local.DatabaseSeeder
import com.nemogz.mantracounter.ui.home.HomeViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { HomeViewModel(get(), get(), get(), get(), get()) }
    single { DatabaseSeeder(get(), get()) }
}
