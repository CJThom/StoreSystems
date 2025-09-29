package com.gpcasiapac.storesystems.common.persistence.db

import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.Dispatchers

actual object RoomDbFinalizer {
    actual fun <T : RoomDatabase> finalize(builder: RoomDatabase.Builder<T>): T {
        return builder
            .setDriver(AndroidSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
