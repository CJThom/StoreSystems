package com.gpcasiapac.storesystems.feature.collect.data.di

import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.getDatabaseBuilder
import com.gpcasiapac.storesystems.feature.collect.data.local.db.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val collectDataDatabaseModule: Module = module {
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}
