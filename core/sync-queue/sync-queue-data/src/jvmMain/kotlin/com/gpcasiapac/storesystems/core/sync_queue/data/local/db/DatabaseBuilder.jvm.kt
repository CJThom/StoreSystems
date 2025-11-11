package com.gpcasiapac.storesystems.core.sync_queue.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getSyncQueueDatabaseBuilder(): RoomDatabase.Builder<SyncQueueDatabase> {
    val dbFile = File(System.getProperty("user.home"), ".storesystems/sync_queue.db")
    dbFile.parentFile?.mkdirs()
    return Room.databaseBuilder<SyncQueueDatabase>(
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver())
}

fun getSyncQueueDatabase(builder: RoomDatabase.Builder<SyncQueueDatabase>): SyncQueueDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()
}
