package com.gpcasiapac.storesystems.app.collect.di

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.app.collect.navigation.CollectAppNavigationViewModel
import com.gpcasiapac.storesystems.app.collect.navigation.globalpatternexample.CollectGlobalNavigationViewModel
import com.gpcasiapac.storesystems.core.identity.data.di.IdentityDataModuleProvider
import com.gpcasiapac.storesystems.core.identity.domain.di.IdentityDomainModuleProvider
import com.gpcasiapac.storesystems.core.sync_queue.data.SyncQueueDataModuleProvider
import com.gpcasiapac.storesystems.core.sync_queue.domain.di.SyncDomainModuleProvider
import com.gpcasiapac.storesystems.external.feature_flags.data.internal.featureFlagModule
import com.gpcasiapac.storesystems.feature.collect.data.di.CollectDataModuleProvider
import com.gpcasiapac.storesystems.feature.collect.domain.di.CollectDomainModuleProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.di.CollectPresentationModuleProvider
import com.gpcasiapac.storesystems.feature.history.presentation.di.HistoryPresentationModuleProvider
import com.gpcasiapac.storesystems.feature.login.domain.di.LoginDomainModuleProvider
import com.gpcasiapac.storesystems.feature.login.presentation.di.LoginPresentationModuleProvider
import com.gpcasiapac.storesystems.foundation.config.di.configModule
import com.gpcasiapac.storesystems.foundation.config.featureflags.FeatureFlagInitializer
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// Expect a platform module that can contribute Android-specific bindings (e.g., ScannerProvider)
expect val collectAppPlatformModule: Module

fun getAppModules(): List<Module> {
    val providerList = listOf(
        IdentityDomainModuleProvider,
        IdentityDataModuleProvider,
        LoginDomainModuleProvider,
        LoginPresentationModuleProvider,
        CollectDataModuleProvider,
        CollectDomainModuleProvider,
        CollectPresentationModuleProvider,
        SyncQueueDataModuleProvider,
        SyncDomainModuleProvider,
        HistoryPresentationModuleProvider
    )

    val moduleList = providerList.flatMap { it.modules() }.toMutableList()

    // Logging: base logger
    moduleList.add(loggingModule)

    // Platform-specific bindings
    moduleList.add(collectAppPlatformModule)

    // Feature flags (MUST be loaded BEFORE appModule that depends on it)
    moduleList.addAll(featureFlagModule)
    moduleList.add(configModule) // FeatureFlagInitializer registered here

    // App-level modules
    moduleList.add(collectAppNavigationModule)
    moduleList.add(appModule)

    return moduleList
}

// App-level Logger binding: one base logger only
val loggingModule = module {
    single<Logger> { Logger.withTag("StoreSystemsLogger") }
}

// App-specific singletons
val appModule = module {

}


val collectAppNavigationModule = module {
    viewModelOf(::CollectAppNavigationViewModel)
    viewModelOf(::CollectGlobalNavigationViewModel)
}
