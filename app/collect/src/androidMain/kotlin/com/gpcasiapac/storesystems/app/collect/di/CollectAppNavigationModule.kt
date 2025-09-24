package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.app.collect.navigation.CollectAppNavigationViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val collectAppNavigationModule = module {
    viewModelOf(::CollectAppNavigationViewModel)
}
