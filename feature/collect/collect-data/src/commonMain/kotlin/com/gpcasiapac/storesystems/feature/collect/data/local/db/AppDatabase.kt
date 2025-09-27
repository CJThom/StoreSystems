package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.OrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.CustomerTypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.TimeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.OrderDao

@Database(
    entities = [OrderEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    TimeConverters::class,
    CustomerTypeConverters::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Suppress("KotlinNoActualForExpect")
expect fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase
