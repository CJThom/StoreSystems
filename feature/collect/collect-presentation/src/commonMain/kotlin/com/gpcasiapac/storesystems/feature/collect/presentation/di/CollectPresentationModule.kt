package com.gpcasiapac.storesystems.feature.collect.presentation.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.workorderdetails.WorkOrderDetailsScreenViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails.OrderDetailsScreenViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.entry.CollectFeatureEntryImpl
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val collectPresentationModule = module {
    viewModelOf(::OrderListScreenViewModel)
    viewModelOf(::OrderFulfilmentScreenViewModel)
    viewModelOf(::WorkOrderDetailsScreenViewModel)
    viewModelOf(::OrderDetailsScreenViewModel)
    viewModelOf(::SignatureScreenViewModel)
    viewModelOf(::CollectNavigationViewModel)

    //factory<CollectFeatureEntry> { CollectFeatureEntryAndroidImpl() }
    singleOf(::CollectFeatureEntryImpl).bind<CollectFeatureEntry>()
}

object CollectPresentationModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectPresentationModule)
}
