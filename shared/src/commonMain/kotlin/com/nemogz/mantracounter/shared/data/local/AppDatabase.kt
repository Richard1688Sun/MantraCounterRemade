package com.nemogz.mantracounter.shared.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.nemogz.mantracounter.shared.data.local.dao.CounterDao
import com.nemogz.mantracounter.shared.data.local.dao.DailyActivityDao
import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseDao
import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseRecipientDao
import com.nemogz.mantracounter.shared.data.local.entity.CounterEntity
import com.nemogz.mantracounter.shared.data.local.entity.DailyActivityEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseEntity
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseRecipientEntity

import com.nemogz.mantracounter.shared.data.local.dao.LittleHouseAllocationDetailsDao
import com.nemogz.mantracounter.shared.data.local.dao.MantraAndHomeworkDetailsDao
import com.nemogz.mantracounter.shared.data.local.entity.LittleHouseAllocationDetailsEntity
import com.nemogz.mantracounter.shared.data.local.entity.MantraAndHomeworkDetailsEntity
import com.nemogz.mantracounter.shared.data.local.dao.AppSettingsDao
import com.nemogz.mantracounter.shared.data.local.entity.AppSettingsEntity

@Database(
    entities = [
        CounterEntity::class,
        LittleHouseEntity::class,
        DailyActivityEntity::class,
        LittleHouseRecipientEntity::class,
        LittleHouseAllocationDetailsEntity::class,
        MantraAndHomeworkDetailsEntity::class,
        AppSettingsEntity::class
    ],
    version = 10
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun littleHouseDao(): LittleHouseDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun littleHouseRecipientDao(): LittleHouseRecipientDao
    abstract fun littleHouseAllocationDetailsDao(): LittleHouseAllocationDetailsDao
    abstract fun mantraAndHomeworkDetailsDao(): MantraAndHomeworkDetailsDao
    abstract fun appSettingsDao(): AppSettingsDao
}


// Expect declaration for platform-specific constructor (standard in Room KMP)
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
