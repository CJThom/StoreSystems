package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.navigation.globalpattern.CollectGlobalNavigationViewModel
import com.gpcasiapac.storesystems.app.collect.navigation.hostpattern.CollectAppNavigationViewModel
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.feature_flags.FlagKey
import com.gpcasiapac.storesystems.core.identity.data.di.identityDataModule
import com.gpcasiapac.storesystems.core.identity.domain.di.identityDomainModule
import com.gpcasiapac.storesystems.feature.collect.data.di.collectDataModuleList
import com.gpcasiapac.storesystems.feature.collect.domain.di.collectDomainModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectFeatureModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationModule
import com.gpcasiapac.storesystems.feature.login.domain.di.loginDomainModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginFeatureModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val collectAppNavigationModule = module {
    viewModelOf(::CollectAppNavigationViewModel)
    viewModelOf(::CollectGlobalNavigationViewModel)
}

val appModule = module {
    single<FeatureFlags> {
        object : FeatureFlags {
            override fun <T> get(key: FlagKey<T>): T = key.default
            override fun <T> observe(key: FlagKey<T>): Flow<T> = flowOf(key.default)
        }
    }
}

