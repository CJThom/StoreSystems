package com.gpcasiapac.storesystems.core.sync_queue.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getSyncQueueDatabaseBuilder(context: Context): RoomDatabase.Builder<SyncQueueDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("sync_queue.db")
    return Room.databaseBuilder<SyncQueueDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

fun getSyncQueueDatabase(builder: RoomDatabase.Builder<SyncQueueDatabase>): SyncQueueDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()
}
