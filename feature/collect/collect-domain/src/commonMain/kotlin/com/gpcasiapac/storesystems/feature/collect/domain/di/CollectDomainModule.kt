package com.gpcasiapac.storesystems.feature.collect.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.sync_queue.api.SyncHandler
import com.gpcasiapac.storesystems.feature.collect.domain.sync.SubmitOrderSyncHandler
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.GetWorkOrderItemsSnapshotUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveCollectOrderWithCustomerWithLineItemsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.GetOrderSearchSuggestionListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveMainOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.search.ObserveSearchOrdersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveOrderCountUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ValidateScannedInvoiceInputUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SaveSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderWithOrderWithCustomersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderItemsInScanOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectionTypeGatingUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveFulfilmentGatingUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCollectingTypeUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCourierNameUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SubmitOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.DeleteWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.RemoveOrderSelectionUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.ObserveCollectUserPrefsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.CreateCollectUserPrefsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.UpdateSelectedWorkOrderIdUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.CreateWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureWorkOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddScannedInputToWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ApplyOrderSelectionDeltaUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureAndApplyOrderSelectionDeltaUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectOrderWithCustomerWithLineItemsListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ToggleOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ToggleAllOrderSelectionUseCase

val collectDomainModule = module {
    // Use cases only; repository bindings are provided by the data module
    factoryOf(::ObserveCollectOrderWithCustomerWithLineItemsUseCase)
    factoryOf(::ObserveCollectOrderWithCustomerWithLineItemsListUseCase)

    factoryOf(::FetchOrderListUseCase)
    factoryOf(::FetchOrderListUseCase)
    factoryOf(::GetOrderSearchSuggestionListUseCase)
    factoryOf(::ObserveMainOrdersUseCase)
    factoryOf(::ObserveSearchOrdersUseCase)
    factoryOf(::ObserveOrderCountUseCase)
    factoryOf(::ValidateScannedInvoiceInputUseCase)

    factoryOf(::SaveSignatureUseCase)
    factoryOf(::ObserveWorkOrderSignatureUseCase)
    factoryOf(::ObserveCollectWorkOrderUseCase)
    factoryOf(::ObserveWorkOrderWithOrderWithCustomersUseCase)
    factoryOf(::ObserveWorkOrderItemsInScanOrderUseCase)
    factoryOf(::ObserveCollectionTypeGatingUseCase)
    factoryOf(::ObserveFulfilmentGatingUseCase)
   // factoryOf(::ObserveCollectOrderWithCustomerWithLineItemsUseCase)
    factoryOf(::SetWorkOrderCollectingTypeUseCase)
    factoryOf(::SetWorkOrderCourierNameUseCase)
    // Selection use cases
    factoryOf(::ObserveOrderSelectionUseCase)

    factoryOf(::AddOrderToCollectWorkOrderUseCase)
    factoryOf(::CreateWorkOrderUseCase)
    factoryOf(::RemoveOrderSelectionUseCase)
    factoryOf(::DeleteWorkOrderUseCase)
    factoryOf(::SubmitOrderUseCase)
    factoryOf(::EnsureWorkOrderSelectionUseCase)
    // Deprecated: EnsureAndAddOrderToWorkOrderUseCase replaced by EnsureAndApplyOrderSelectionDeltaUseCase
    // factoryOf(::EnsureAndAddOrderToWorkOrderUseCase)

    factoryOf(::ApplyOrderSelectionDeltaUseCase)
    factoryOf(::EnsureAndApplyOrderSelectionDeltaUseCase)
    factoryOf(::AddScannedInputToWorkOrderUseCase)
    // Selection calculators as use cases
    factoryOf(::ToggleOrderSelectionUseCase)
    factoryOf(::ToggleAllOrderSelectionUseCase)

    // Preferences use cases
//    factoryOf(::GetSelectedWorkOrderIdFlowUseCase)
//    factoryOf(::SetSelectedWorkOrderIdUseCase)
//    factoryOf(::GetCollectFiltersFlowUseCase)
//    factoryOf(::SetCollectFiltersUseCase)
    factoryOf(::ObserveCollectUserPrefsUseCase)
    factoryOf(::CreateCollectUserPrefsUseCase)
    factoryOf(::GetCollectSessionIdsFlowUseCase)
    factoryOf(::UpdateSelectedWorkOrderIdUseCase)

    factoryOf(::GetWorkOrderItemsSnapshotUseCase)
    // Register Collect feature's SyncHandler(s)
    factory<SyncHandler> { SubmitOrderSyncHandler(get()) }
}

object CollectDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectDomainModule)
}
