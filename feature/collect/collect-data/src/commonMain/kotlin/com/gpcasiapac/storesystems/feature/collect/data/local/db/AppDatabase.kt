package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.CustomerTypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.TimeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.WorkOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        CollectOrderEntity::class,
        CollectOrderCustomerEntity::class,
        CollectOrderLineItemEntity::class,
        CollectWorkOrderEntity::class,
        CollectWorkOrderItemEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(
    TimeConverters::class,
    CustomerTypeConverters::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectOrderDao(): CollectOrderDao
    abstract fun workOrderDao(): WorkOrderDao
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
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
