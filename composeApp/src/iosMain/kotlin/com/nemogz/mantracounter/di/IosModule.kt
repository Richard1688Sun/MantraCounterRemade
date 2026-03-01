package com.nemogz.mantracounter.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.nemogz.mantracounter.shared.data.local.AppDatabase
import com.nemogz.mantracounter.shared.data.local.instantiateImpl
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): androidx.room.RoomDatabase.Builder<AppDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val dbFilePath = requireNotNull(documentDirectory?.path) + "/mantra_counter.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
        factory =  { AppDatabase::class.instantiateImpl() }
    ).setDriver(BundledSQLiteDriver())
}

val iosModule = module {
    single<AppDatabase> {
        getDatabaseBuilder()
            .fallbackToDestructiveMigration(true)
            .build()
    }
}
