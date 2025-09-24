package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.navigation.CollectGlobalNavigationViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val collectGlobalNavigationModule = module {
    // Global, single-stack navigation VM
    viewModelOf(::CollectGlobalNavigationViewModel)
}
