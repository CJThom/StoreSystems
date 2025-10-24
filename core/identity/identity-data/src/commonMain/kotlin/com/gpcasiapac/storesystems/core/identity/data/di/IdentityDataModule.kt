package com.gpcasiapac.storesystems.core.identity.data.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.identity.data.network.source.IdentityNetworkDataSource
import com.gpcasiapac.storesystems.core.identity.data.network.source.MockIdentityNetworkDataSourceImpl
import com.gpcasiapac.storesystems.core.identity.data.repository.IdentityRepositoryImpl
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val identityDataModule = module {
    singleOf(::MockIdentityNetworkDataSourceImpl) { bind<IdentityNetworkDataSource>() }
    singleOf(::IdentityRepositoryImpl) { bind<IdentityRepository>() }
}

object IdentityDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(
        identityDataModule,
        identityDataDatabaseModule,
        identityDaoModule,
    )
}
