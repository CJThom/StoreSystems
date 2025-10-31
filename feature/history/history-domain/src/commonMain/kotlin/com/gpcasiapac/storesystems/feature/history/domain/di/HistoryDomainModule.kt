package com.gpcasiapac.storesystems.feature.history.domain.di

import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.ObserveCollectHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.ObserveUnifiedHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.RetryHistoryUseCase
import com.gpcasiapac.storesystems.feature.history.domain.usecase.GetCollectHistoryItemByIdUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val historyDomainModule = module {
    factoryOf(::ObserveCollectHistoryUseCase)
    factoryOf(::ObserveUnifiedHistoryUseCase)
    factoryOf(::GetHistoryUseCase)
    factoryOf(::RetryHistoryUseCase)
    factoryOf(::GetCollectHistoryItemByIdUseCase)
}
