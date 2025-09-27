package com.gpcasiapac.storesystems.feature.collect.data.di

import android.content.Context
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.getDatabaseBuilder
import com.gpcasiapac.storesystems.feature.collect.data.local.db.getRoomDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val collectDataAndroidModule: Module = module {
    // Expect a Context to be provided by the host app Koin (we'll register it in app modules)
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(get<Context>())) }

    // DAO
    single { get<AppDatabase>().orderDao() }
}
