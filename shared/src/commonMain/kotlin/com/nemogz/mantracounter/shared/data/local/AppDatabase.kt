package com.nemogz.mantracounter.shared.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.nemogz.mantracounter.shared.data.local.dao.CounterDao
import com.nemogz.mantracounter.shared.data.local.dao.DailyActivityDao
import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseDao
import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseRecipientDao
import com.nemogz.mantracounter.shared.data.local.entity.CounterEntity
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseRecipientEntity

@Database(
    entities = [
        CounterEntity::class,
        LittleHouseEntity::class,
        DailyActivityEntity::class,
        LittleHouseRecipientEntity::class
    ],
    version = 5
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun littleHouseDao(): LittleHouseDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun littleHouseRecipientDao(): LittleHouseRecipientDao
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        // Create the new little_house_recipients table
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS little_house_recipients (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                goal INTEGER NOT NULL DEFAULT 0,
                targetFinishDate INTEGER,
                burnedCount INTEGER NOT NULL DEFAULT 0,
                sortOrder INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
        // Add littleHouseBurnDetails column to daily_activity
        connection.execSQL(
            "ALTER TABLE daily_activity ADD COLUMN littleHouseBurnDetails TEXT NOT NULL DEFAULT ''"
        )
    }
}

// Expect declaration for platform-specific constructor (standard in Room KMP)
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
