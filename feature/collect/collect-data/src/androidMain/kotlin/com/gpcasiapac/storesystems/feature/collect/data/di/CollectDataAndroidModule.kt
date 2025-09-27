package com.gpcasiapac.storesystems.feature.collect.data.di

import com.gpcasiapac.storesystems.common.networking.resources.ResourceReader
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.getDatabaseBuilder
import com.gpcasiapac.storesystems.feature.collect.data.local.db.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

val collectDataAndroidModule: Module = module {

    // Build DB using Android builder (requires Context) and finalize with Android driver via RoomDbFinalizer
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(get())) }

    // DAO
    single { get<AppDatabase>().orderDao() }
}
