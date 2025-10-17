package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.AddTaskToQueueUseCase
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.CollectTaskUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val syncQueueDomainModule = module {
    factoryOf(::AddTaskToQueueUseCase)
    factoryOf(::CollectTaskUseCase)
}