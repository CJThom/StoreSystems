package com.gpcasiapac.storesystems.feature.collect.data.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.data.network.source.MockOrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.data.repository.OrderRepositoryImpl
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val collectDataModule = module {
    // Common mock network data source loads JSON from commonMain/resources
    singleOf(::MockOrderNetworkDataSource) { bind<OrderNetworkDataSource>() }

    // Repository binding (real implementation backed by DB + network).
    singleOf(::OrderRepositoryImpl) { bind<OrderRepository>() }
}

object CollectDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectDataModule)
}
