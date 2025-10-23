package com.gpcasiapac.storesystems.feature.history.domain.di

import com.gpcasiapac.storesystems.feature.history.domain.usecase.DeleteHistoryItemUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryByEntityIdUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.RetryHistoryItemUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val historyDomainModule = module {
    factoryOf(::GetHistoryUseCase)
    factoryOf(::GetHistoryByEntityIdUseCase)
    factoryOf(::DeleteHistoryItemUseCase)
    factoryOf(::RetryHistoryItemUseCase)
}
