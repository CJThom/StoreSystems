package com.gpcasiapac.storesystems.core.identity.data.di

import com.gpcasiapac.storesystems.core.identity.data.local.db.IdentityDatabase
import com.gpcasiapac.storesystems.core.identity.data.local.db.getDatabaseBuilder
import com.gpcasiapac.storesystems.core.identity.data.local.db.getRoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val identityDataDatabaseModule: Module = module {
    single<IdentityDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}