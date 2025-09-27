package com.gpcasiapac.storesystems.common.persistence.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

actual object RoomDbFinalizer {
    actual fun <T : RoomDatabase> finalize(builder: RoomDatabase.Builder<T>): T {
        return builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
