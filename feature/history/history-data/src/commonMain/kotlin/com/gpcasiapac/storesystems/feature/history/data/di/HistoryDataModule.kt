package com.gpcasiapac.storesystems.feature.history.data.di

import com.gpcasiapac.storesystems.feature.history.data.repository.HistoryRepositoryImpl
import com.gpcasiapac.storesystems.feature.history.domain.repository.HistoryRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val historyDataModule = module {
    singleOf(::HistoryRepositoryImpl).bind<HistoryRepository>()
}
