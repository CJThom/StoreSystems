package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import androidx.sqlite.driver.AndroidSQLiteDriver

actual fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(AndroidSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
