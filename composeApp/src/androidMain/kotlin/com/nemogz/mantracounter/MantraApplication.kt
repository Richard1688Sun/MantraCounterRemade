package com.nemogz.mantracounter

import android.app.Application
import com.nemogz.mantracounter.di.androidModule
import com.nemogz.mantracounter.di.viewModelModule
import com.nemogz.mantracounter.shared.di.featureModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
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

        // Seed Database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val seeder: com.nemogz.mantracounter.shared.data.local.DatabaseSeeder by inject()
                seeder.seed()
            } catch (e: Exception) {
                android.util.Log.e("MantraApp", "Seeder failed", e)
            }
        }
    }
}
