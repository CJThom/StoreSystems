package com.gpcasiapac.storesystems.app.superapp.di

import com.gpcasiapac.storesystems.app.superapp.navigation.SuperGlobalNavigationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val superGlobalNavigationModule: Module = module {
    viewModelOf(::SuperGlobalNavigationViewModel)
}
