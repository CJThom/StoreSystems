package com.gpcasiapac.storesystems.feature.collect.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.feature.collect.domain.sync.SubmitOrderSyncHandler
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveCollectOrderWithCustomerWithLineItemsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveMainOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveOrderCountUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.CheckOrderExistsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SaveSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderWithOrderWithCustomersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderItemsInScanOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCollectingTypeUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCourierNameUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SubmitOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.DeleteWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderListToCollectWorkOrderUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.ObserveCollectUserPrefsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.SaveCollectUserPrefsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase

val collectDomainModule = module {
    // Use cases only; repository bindings are provided by the data module
    factoryOf(::ObserveCollectOrderWithCustomerWithLineItemsUseCase)
    factoryOf(::FetchOrderListUseCase)
    factoryOf(::GetOrderSearchSuggestionListUseCase)
    factoryOf(::ObserveMainOrdersUseCase)
    factoryOf(::ObserveSearchOrdersUseCase)
    factoryOf(::ObserveOrderCountUseCase)
    factoryOf(::CheckOrderExistsUseCase)

    factoryOf(::SaveSignatureUseCase)
    factoryOf(::ObserveCollectWorkOrderUseCase)
    factoryOf(::ObserveWorkOrderWithOrderWithCustomersUseCase)
    factoryOf(::ObserveWorkOrderItemsInScanOrderUseCase)
    factoryOf(::SetWorkOrderCollectingTypeUseCase)
    factoryOf(::SetWorkOrderCourierNameUseCase)
    // Selection use cases
    factoryOf(::ObserveOrderSelectionUseCase)
    factoryOf(::AddOrderListToCollectWorkOrderUseCase)
    factoryOf(::AddOrderToCollectWorkOrderUseCase)
    factoryOf(::RemoveOrderSelectionUseCase)
    factoryOf(::DeleteWorkOrderUseCase)
    factoryOf(::SubmitOrderUseCase)

    // Preferences use cases
//    factoryOf(::GetSelectedWorkOrderIdFlowUseCase)
//    factoryOf(::SetSelectedWorkOrderIdUseCase)
//    factoryOf(::GetCollectFiltersFlowUseCase)
//    factoryOf(::SetCollectFiltersUseCase)
    factoryOf(::ObserveCollectUserPrefsUseCase)
    factoryOf(::SaveCollectUserPrefsUseCase)
    factoryOf(::GetCollectSessionIdsFlowUseCase)

    // Register Collect feature's SyncHandler(s)
    factory<SyncHandler> { SubmitOrderSyncHandler(get()) }
}

object CollectDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectDomainModule)
}
