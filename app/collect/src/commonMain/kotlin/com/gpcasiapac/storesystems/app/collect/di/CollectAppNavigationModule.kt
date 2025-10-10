package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.navigation.globalpatternexample.CollectGlobalNavigationViewModel
import com.gpcasiapac.storesystems.app.collect.navigation.CollectAppNavigationViewModel
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.feature_flags.FlagKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

