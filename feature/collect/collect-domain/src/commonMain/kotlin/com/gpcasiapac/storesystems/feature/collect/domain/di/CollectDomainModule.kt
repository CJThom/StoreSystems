package com.gpcasiapac.storesystems.feature.collect.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.domain.repo.OrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repo.fake.FakeOrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.RefreshOrdersUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val collectDomainModule = module {
    // Repository (fake for now)
    singleOf(::FakeOrderRepository) { bind<OrderRepository>() }

    // Use cases
    factoryOf(::ObserveOrderListUseCase)
    factoryOf(::RefreshOrdersUseCase)
}

object CollectDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectDomainModule)
}
