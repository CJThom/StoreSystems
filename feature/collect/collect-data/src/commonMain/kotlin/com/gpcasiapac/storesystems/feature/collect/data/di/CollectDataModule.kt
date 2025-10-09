package com.gpcasiapac.storesystems.feature.collect.data.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.network.source.MockOrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.repository.OrderRepositoryImpl
import com.gpcasiapac.storesystems.feature.collect.data.repository.OrderSelectionRepositoryImpl
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderSelectionRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val collectDataDatabaseModule: Module

val daoModule: Module = module {
    single<CollectOrderDao> { get<AppDatabase>().collectOrderDao() }
}

val collectDataModule = module {
    // Common mock network data source loads JSON from commonMain/resources
    singleOf(::MockOrderNetworkDataSource) { bind<OrderNetworkDataSource>() }

    // Repository bindings (real implementations backed by DB + network).
    singleOf(::OrderRepositoryImpl) { bind<OrderRepository>() }

    // Selection repository (in-memory for now; swap to persistent later)
    singleOf(::OrderSelectionRepositoryImpl) { bind<OrderSelectionRepository>() }
}

val collectDataModuleList: List<Module>
    get() = listOf(
        collectDataModule,
        collectDataDatabaseModule,
        daoModule
    )

object CollectDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> =
        listOf(
            collectDataModule,
            collectDataDatabaseModule,
            daoModule
        )
}
