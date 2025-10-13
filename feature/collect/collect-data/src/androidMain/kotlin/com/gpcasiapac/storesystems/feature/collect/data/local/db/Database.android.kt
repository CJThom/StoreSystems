package com.gpcasiapac.storesystems.feature.collect.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbName = "collect.db"
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbName,
    ).setDriver(AndroidSQLiteDriver())
}
