package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.core.identity.data.di.IdentityDataModuleProvider
import com.gpcasiapac.storesystems.core.identity.domain.di.IdentityDomainModuleProvider
import com.gpcasiapac.storesystems.feature.collect.data.di.CollectDataModuleProvider
import com.gpcasiapac.storesystems.feature.collect.domain.di.CollectDomainModuleProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.di.CollectFeatureModuleProvider
import com.gpcasiapac.storesystems.feature.collect.presentation.di.CollectPresentationModuleProvider
import com.gpcasiapac.storesystems.feature.login.domain.di.LoginDomainModuleProvider
import com.gpcasiapac.storesystems.feature.login.presentation.di.LoginFeatureModuleProvider
import com.gpcasiapac.storesystems.feature.login.presentation.di.LoginPresentationModuleProvider
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(appDeclaration: KoinApplication.() -> Unit = {}) {
    startKoin {
        appDeclaration()
        modules(CollectDataModuleProvider.modules())
        modules(LoginFeatureModuleProvider.modules())
        modules(CollectFeatureModuleProvider.modules())
        modules(IdentityDomainModuleProvider.modules())
        modules(IdentityDataModuleProvider.modules())
        modules(LoginDomainModuleProvider.modules())
        modules(LoginPresentationModuleProvider.modules())
        modules(CollectPresentationModuleProvider.modules())
        modules(CollectDomainModuleProvider.modules())
        modules(
            collectAppNavigationModule,
            appModule,
        )
    }
}
