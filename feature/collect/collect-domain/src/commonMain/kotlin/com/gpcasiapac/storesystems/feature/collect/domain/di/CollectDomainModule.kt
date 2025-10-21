package com.gpcasiapac.storesystems.feature.collect.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.sync_queue.domain.SyncHandler
import com.gpcasiapac.storesystems.feature.collect.domain.sync.SubmitOrderSyncHandler
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetCollectOrderWithCustomerListFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetCollectOrderWithCustomerWithLineItemsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveMainOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderSelectionResultUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveOrderCountUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.SaveSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.AddOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ClearOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.SetOrderSelectionUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val collectDomainModule = module {
    // Use cases only; repository bindings are provided by the data module
    factoryOf(::GetCollectOrderWithCustomerWithLineItemsFlowUseCase)
    factoryOf(::FetchOrderListUseCase)
    factoryOf(::GetOrderSearchSuggestionListUseCase)
    factoryOf(::ObserveMainOrdersUseCase)
    factoryOf(::ObserveSearchOrdersUseCase)
    factoryOf(::ObserveOrderCountUseCase)

    factoryOf(::GetCollectOrderWithCustomerListFlowUseCase)
    factoryOf(::ObserveOrderSelectionResultUseCase)

    factoryOf(::SaveSignatureUseCase)
    // Selection use cases
    factoryOf(::ObserveOrderSelectionUseCase)
    factoryOf(::SetOrderSelectionUseCase)
    factoryOf(::AddOrderSelectionUseCase)
    factoryOf(::RemoveOrderSelectionUseCase)
    factoryOf(::ClearOrderSelectionUseCase)

    // Register Collect feature's SyncHandler(s)
    factory<SyncHandler> { SubmitOrderSyncHandler(get()) }
}

object CollectDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectDomainModule)
}
