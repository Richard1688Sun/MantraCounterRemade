package com.nemogz.mantracounter.di

import android.content.Context
import androidx.room.Room
import com.nemogz.mantracounter.shared.data.local.AppDatabase
import com.nemogz.mantracounter.shared.data.local.MIGRATION_4_5
import org.koin.dsl.module

fun androidModule(context: Context) = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            context = context.applicationContext,
            klass = AppDatabase::class.java,
            name = "mantra_counter.db"
        )
        .addMigrations(MIGRATION_4_5)
        .fallbackToDestructiveMigration()
        .build()
    }
}
