package com.gpcasiapac.storesystems.core.identity.data.di

import com.gpcasiapac.storesystems.core.identity.data.local.db.IdentityDatabase
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.AuthSessionDao
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.IdentityUserDao
import org.koin.core.module.Module
import org.koin.dsl.module

expect val identityDataDatabaseModule: Module

val identityDaoModule: Module = module {
    single<IdentityUserDao> { get<IdentityDatabase>().userDao() }
    single<AuthSessionDao> { get<IdentityDatabase>().sessionDao() }
}
