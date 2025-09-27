package com.gpcasiapac.storesystems.feature.collect.data.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.data.repository.FakeOrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val collectDataModule = module {
    // Repository binding (fake implementation for now)
    singleOf(::FakeOrderRepository) { bind<OrderRepository>() }
}

object CollectDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectDataModule)
}
