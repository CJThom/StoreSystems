package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gpcasiapac.storesystems.common.persistence.db.RoomDbFinalizer
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.CustomerTypeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.converter.TimeConverters
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderCustomerEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderEntity
import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.CollectOrderLineItemEntity

@Database(
    entities = [
        CollectOrderEntity::class,
        CollectOrderCustomerEntity::class,
        CollectOrderLineItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    TimeConverters::class,
    CustomerTypeConverters::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectOrderDao(): CollectOrderDao
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return RoomDbFinalizer.finalize(builder)
}
