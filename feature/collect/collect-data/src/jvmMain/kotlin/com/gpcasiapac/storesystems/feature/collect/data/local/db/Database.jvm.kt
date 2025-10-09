package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "collect.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}

//fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
//    //val dbFile = File(System.getProperty("java.io.tmpdir"), "shoppingList.db")
//    val userHome = System.getProperty("collect")
//    val dbFile = File(userHome, "AndroidStudioProjects/HouseHelper/databases/messenger_desktop.db")
//
//    return Room.databaseBuilder<AppDatabase>(
//        name = dbFile.absolutePath,
//    ).setDriver(BundledSQLiteDriver())
//}