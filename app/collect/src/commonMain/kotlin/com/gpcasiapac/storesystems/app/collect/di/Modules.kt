package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.navigation.CollectAppNavigationViewModel
import com.gpcasiapac.storesystems.app.collect.navigation.globalpatternexample.CollectGlobalNavigationViewModel
import com.gpcasiapac.storesystems.core.sync_queue.data.syncQueueDataModule
import com.gpcasiapac.storesystems.core.sync_queue.domain.syncQueueDomainModule
import com.gpcasiapac.storesystems.external.feature_flags.data.internal.featureFlagModule
import com.gpcasiapac.storesystems.core.identity.data.di.IdentityDataModuleProvider
import com.gpcasiapac.storesystems.core.identity.domain.di.IdentityDomainModuleProvider
import com.gpcasiapac.storesystems.feature.collect.data.di.CollectDataModuleProvider
import com.gpcasiapac.storesystems.feature.collect.domain.di.CollectDomainModuleProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.di.CollectPresentationModuleProvider
import com.gpcasiapac.storesystems.feature.login.domain.di.LoginDomainModuleProvider
import com.gpcasiapac.storesystems.feature.login.presentation.di.LoginPresentationModuleProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun getAppModules(): List<Module> {
    val providerList = listOf(
        IdentityDomainModuleProvider,
        IdentityDataModuleProvider,
        LoginDomainModuleProvider,
        LoginPresentationModuleProvider,
        CollectDataModuleProvider,
        CollectDomainModuleProvider,
        CollectPresentationModuleProvider,
    )

    val moduleList = providerList.flatMap { it.modules() }.toMutableList()

    // Add sync queue modules
    moduleList.add(syncQueueDataModule)
    moduleList.add(syncQueueDomainModule)

    // App-level modules
    moduleList.add(collectAppNavigationModule)
    moduleList.add(appModule)
    moduleList.addAll(featureFlagModule)

    // Platform-specific worker modules
    moduleList.addAll(workerModules())

    return moduleList
}

val appModule = module {}

val collectAppNavigationModule = module {
    viewModelOf(::CollectAppNavigationViewModel)
    viewModelOf(::CollectGlobalNavigationViewModel)
}
