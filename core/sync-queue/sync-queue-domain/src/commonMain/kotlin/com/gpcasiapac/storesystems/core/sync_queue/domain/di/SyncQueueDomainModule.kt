package com.gpcasiapac.storesystems.core.sync_queue.domain.di

import com.gpcasiapac.storesystems.core.sync_queue.api.SyncQueueService
import com.gpcasiapac.storesystems.core.sync_queue.domain.SyncQueueServiceImpl
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.AddTaskAndTriggerSyncUseCase
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.CollectTaskUseCase
import com.gpcasiapac.storesystems.core.sync_queue.domain.usecase.EnqueueCollectTaskAndTriggerSyncUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val syncQueueDomainModule = module {
    factoryOf(::AddTaskAndTriggerSyncUseCase)
    factoryOf(::EnqueueCollectTaskAndTriggerSyncUseCase)
    factoryOf(::CollectTaskUseCase)
    
    // Public service facade
    factoryOf(::SyncQueueServiceImpl) bind SyncQueueService::class
    
    // SyncTriggerCoordinator will be provided by platform-specific modules
}