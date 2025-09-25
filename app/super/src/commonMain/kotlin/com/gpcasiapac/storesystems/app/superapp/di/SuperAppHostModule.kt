package com.gpcasiapac.storesystems.app.superapp.di

import com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern.SuperAppShellViewModel
import com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern.TabsHostViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val superAppHostModule: Module = module {
    viewModelOf(::SuperAppShellViewModel)
    viewModelOf(::TabsHostViewModel)
}
