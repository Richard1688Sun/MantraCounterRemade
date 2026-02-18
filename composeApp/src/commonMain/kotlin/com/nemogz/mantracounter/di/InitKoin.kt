package com.nemogz.mantracounter.di

import com.nemogz.mantracounter.shared.di.featureModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            featureModule,
            viewModelModule,
        )
    }
}