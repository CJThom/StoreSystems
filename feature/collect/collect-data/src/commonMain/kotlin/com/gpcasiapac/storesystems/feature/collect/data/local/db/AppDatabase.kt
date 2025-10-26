package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.CustomerTypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.TimeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.SortOptionConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.WorkOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectUserPrefsDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectWorkOrderItemEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectUserPrefsEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        CollectOrderEntity::class,
        CollectOrderCustomerEntity::class,
        CollectOrderLineItemEntity::class,
        CollectWorkOrderEntity::class,
        CollectWorkOrderItemEntity::class,
        CollectUserPrefsEntity::class,
    ],
    version = 5,
    exportSchema = true,
)
@TypeConverters(
    TimeConverters::class,
    CustomerTypeConverters::class,
    SortOptionConverters::class,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectOrderDao(): CollectOrderDao
    abstract fun workOrderDao(): WorkOrderDao
    abstract fun collectUserPrefsDao(): CollectUserPrefsDao
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
    val db = builder
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()
    // Note: We rely on Room to configure journal mode at open.
    // Avoid manual warm-up here to keep commonMain platform-agnostic.
    return db
}
