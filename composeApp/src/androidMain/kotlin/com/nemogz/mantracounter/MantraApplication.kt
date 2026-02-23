package com.nemogz.mantracounter

import android.app.Application
import com.nemogz.mantracounter.di.androidModule
import com.nemogz.mantracounter.di.viewModelModule
import com.nemogz.mantracounter.shared.di.featureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MantraApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MantraApplication)
            modules(
                androidModule(this@MantraApplication),
                featureModule,
                viewModelModule
            )
        }
    }
}
