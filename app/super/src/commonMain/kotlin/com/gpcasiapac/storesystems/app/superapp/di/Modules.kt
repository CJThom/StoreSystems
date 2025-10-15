package com.gpcasiapac.storesystems.app.superapp.di

import com.gpcasiapac.storesystems.app.superapp.navigation.SuperAppShellViewModel
import com.gpcasiapac.storesystems.app.superapp.navigation.TabsHostViewModel
import com.gpcasiapac.storesystems.app.superapp.navigation.globalpatternexample.SuperGlobalNavigationViewModel
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.data.FeatureFlagsFactory
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
        CollectDataModuleProvider,
        CollectDomainModuleProvider,
        CollectPresentationModuleProvider,
    )

    val moduleList = providerList.flatMap { it.modules() }.toMutableList()

    // TODO: Use ModuleProvider
    moduleList.add(superGlobalNavigationModule)
    moduleList.add(appModule)

    return moduleList
}

val appModule = module {
    single<FeatureFlags> {
        FeatureFlagsFactory.createNoOp()
    }
}


val superGlobalNavigationModule: Module = module {
    viewModelOf(::SuperGlobalNavigationViewModel)
    viewModelOf(::SuperAppShellViewModel)
    viewModelOf(::TabsHostViewModel)
}
