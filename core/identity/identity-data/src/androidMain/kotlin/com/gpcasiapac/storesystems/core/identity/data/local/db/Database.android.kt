package com.gpcasiapac.storesystems.core.identity.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<IdentityDatabase> {
    val appContext = context.applicationContext
    val dbName = "identity.db"
    return Room.databaseBuilder<IdentityDatabase>(
        context = appContext,
        name = dbName,
    ).setDriver(AndroidSQLiteDriver())
}
