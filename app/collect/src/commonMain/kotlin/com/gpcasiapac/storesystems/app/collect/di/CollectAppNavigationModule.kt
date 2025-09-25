package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.navigation.hostpattern.CollectAppNavigationViewModel
import com.gpcasiapac.storesystems.app.collect.navigation.globalpattern.CollectGlobalNavigationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val collectAppNavigationModule = module {
    viewModelOf(::CollectAppNavigationViewModel)
    viewModelOf(::CollectGlobalNavigationViewModel)
}