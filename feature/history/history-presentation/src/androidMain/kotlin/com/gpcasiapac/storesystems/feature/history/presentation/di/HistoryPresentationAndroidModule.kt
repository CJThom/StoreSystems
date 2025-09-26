package com.gpcasiapac.storesystems.feature.history.presentation.di

import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureEntry
import com.gpcasiapac.storesystems.feature.history.presentation.entry.HistoryFeatureEntryAndroidImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val historyPresentationAndroidModule = module {
    singleOf(::HistoryFeatureEntryAndroidImpl).bind<HistoryFeatureEntry>()
}
