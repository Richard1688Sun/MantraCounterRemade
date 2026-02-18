package com.nemogz.mantracounter.shared.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.nemogz.mantracounter.shared.data.local.dao.CounterDao
import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseDao
import com.nemogz.mantracounter.shared.data.local.entity.CounterEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseEntity

@Database(entities = [CounterEntity::class, LittleHouseEntity::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun littleHouseDao(): LittleHouseDao
}

// Expect declaration for platform-specific constructor (standard in Room KMP)
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
