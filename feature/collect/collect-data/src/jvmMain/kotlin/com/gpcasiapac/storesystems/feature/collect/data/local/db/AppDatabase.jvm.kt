package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
