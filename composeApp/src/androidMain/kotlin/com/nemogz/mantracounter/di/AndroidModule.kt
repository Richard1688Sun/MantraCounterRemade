package com.nemogz.mantracounter.di

import android.content.Context
import androidx.room.Room
import com.nemogz.mantracounter.shared.data.local.AppDatabase
import org.koin.dsl.module

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_8 = object : Migration(6, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE little_house ADD COLUMN name TEXT NOT NULL DEFAULT 'Little House'")
        db.execSQL("ALTER TABLE daily_activity ADD COLUMN littleHouseManualIncrease INTEGER NOT NULL DEFAULT 0")
    }
}
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Technically DailyActivity change wasn't recorded in V7 schema natively because of the hash check failure
        // Depending on if V7 got installed on the user device, we need a 7->8 path as well.
        // The safest approach is to use fallbackToDestructiveMigration if we want, or add a column if it doesn't exist
        try {
            db.execSQL("ALTER TABLE daily_activity ADD COLUMN littleHouseManualIncrease INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            // Column might already exist if 6_7 accidentally partially ran
        }
    }
}
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE daily_activity ADD COLUMN littleHouseStartCount INTEGER NOT NULL DEFAULT 0")
        } catch (e: Exception) {
            // Might exist if previous fallback behavior partially ran
        }
    }
}

fun androidModule(context: Context) = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            context = context.applicationContext,
            klass = AppDatabase::class.java,
            name = "mantra_counter.db"
        )
        .addMigrations(MIGRATION_6_8, MIGRATION_7_8, MIGRATION_8_9)
        .fallbackToDestructiveMigration()
        .build()
    }
}
