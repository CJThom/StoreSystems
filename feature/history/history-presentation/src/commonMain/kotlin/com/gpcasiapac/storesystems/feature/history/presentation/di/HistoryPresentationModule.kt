package com.gpcasiapac.storesystems.feature.history.presentation.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureEntry
import com.gpcasiapac.storesystems.feature.history.presentation.entry.HistoryFeatureEntryImpl
import com.gpcasiapac.storesystems.feature.history.presentation.navigation.HistoryNavigationViewModel
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyPresentationModule = module {
    viewModelOf(::HistoryScreenViewModel)
    viewModelOf(::HistoryNavigationViewModel)

    singleOf(::HistoryFeatureEntryImpl) { bind<HistoryFeatureEntry>() }
}

object HistoryPresentationModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(historyPresentationModule)
}
