package com.gpcasiapac.storesystems.core.sync_queue.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.converter.SyncQueueTypeConverters
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.converter.TimeConverters
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao.SyncTaskDao
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.entity.SyncTaskEntity

@Database(
    entities = [SyncTaskEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(TimeConverters::class, SyncQueueTypeConverters::class)
@ConstructedBy(SyncQueueDatabaseConstructor::class)
abstract class SyncQueueDatabase : RoomDatabase() {
    abstract fun syncTaskDao(): SyncTaskDao
}

@Suppress("KotlinNoActualForExpect")
expect object SyncQueueDatabaseConstructor : RoomDatabaseConstructor<SyncQueueDatabase> {
    override fun initialize(): SyncQueueDatabase
}
