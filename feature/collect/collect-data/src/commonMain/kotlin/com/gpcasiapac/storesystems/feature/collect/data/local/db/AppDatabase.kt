package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.gpcasiapac.storesystems.common.persistence.db.RoomDbFinalizer
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.CustomerTypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.TimeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        CollectOrderEntity::class,
        CollectOrderCustomerEntity::class,
        CollectOrderLineItemEntity::class,
        CollectWorkOrderEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    TimeConverters::class,
    CustomerTypeConverters::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectOrderDao(): CollectOrderDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

//fun getRoomDatabase(
//    builder: RoomDatabase.Builder<AppDatabase>
//): AppDatabase {
//    return RoomDbFinalizer.finalize(builder)
//}
fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
