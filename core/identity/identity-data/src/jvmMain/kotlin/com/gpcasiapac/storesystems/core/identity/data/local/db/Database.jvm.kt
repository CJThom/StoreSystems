package com.gpcasiapac.storesystems.core.identity.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<IdentityDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "identity.db")
    return Room.databaseBuilder<IdentityDatabase>(
        name = dbFile.absolutePath,
    ).setDriver(BundledSQLiteDriver())
}
