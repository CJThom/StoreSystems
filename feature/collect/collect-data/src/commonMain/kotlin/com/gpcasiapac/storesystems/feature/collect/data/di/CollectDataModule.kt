package com.gpcasiapac.storesystems.feature.collect.data.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.data.local.db.AppDatabase
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.WorkOrderDao
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectUserPrefsDao
import com.gpcasiapac.storesystems.feature.collect.data.network.source.MockOrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.repository.OrderRepositoryImpl
import com.gpcasiapac.storesystems.feature.collect.data.repository.CollectUserPrefsRepositoryImpl
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val collectDataDatabaseModule: Module

val daoModule: Module = module {
    single<CollectOrderDao> { get<AppDatabase>().collectOrderDao() }
    single<WorkOrderDao> { get<AppDatabase>().workOrderDao() }
    single<CollectUserPrefsDao> { get<AppDatabase>().collectUserPrefsDao() }
}

val collectDataModule = module {
    // Common mock network data source loads JSON from commonMain/resources
    singleOf(::MockOrderNetworkDataSource) { bind<OrderNetworkDataSource>() }

    // Repository bindings (real implementations backed by DB + network).
    singleOf(::OrderRepositoryImpl) { bind<OrderRepository>() }
    singleOf(::CollectUserPrefsRepositoryImpl) { bind<CollectUserPrefsRepository>() }
}

object CollectDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> =
        listOf(
            collectDataModule,
            collectDataDatabaseModule,
            daoModule
        )
}
