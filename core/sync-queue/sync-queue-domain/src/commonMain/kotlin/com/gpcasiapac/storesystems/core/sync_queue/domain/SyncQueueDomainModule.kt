package com.gpcasiapac.storesystems.core.sync_queue.domain

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.AddTaskAndTriggerSyncUseCase
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.AddTaskToQueueUseCase
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.CollectTaskUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val syncQueueDomainModule = module {
    factoryOf(::AddTaskToQueueUseCase)
    factoryOf(::AddTaskAndTriggerSyncUseCase)
    factoryOf(::CollectTaskUseCase)
    
    // Public service facade
    factoryOf(::SyncQueueServiceImpl) bind SyncQueueService::class
    
    // SyncTriggerCoordinator will be provided by platform-specific modules
}