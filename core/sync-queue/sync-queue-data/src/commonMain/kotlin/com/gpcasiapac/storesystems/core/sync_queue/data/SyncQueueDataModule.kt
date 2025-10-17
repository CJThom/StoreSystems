package com.gpcasiapac.storesystems.core.sync_queue.data

import com.gpcasiapac.storesystems.core.sync_queue.data.repository.SyncRepositoryImpl
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val syncQueueDataModule = module {
    singleOf(::SyncRepositoryImpl) { bind<SyncRepository>() }
}