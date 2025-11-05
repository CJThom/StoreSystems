package com.gpcasiapac.storesystems.core.sync_queue.data

import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.SyncQueueDatabase
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao.CollectTaskMetadataDao
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.dao.SyncTaskDao
import com.gpcasiapac.storesystems.core.sync_queue.data.repository.SyncRepositoryImpl
import com.gpcasiapac.storesystems.core.sync_queue.domain.repository.SyncRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// Platform-specific database module
expect val syncQueueDatabaseModule: Module

// DAO module
val syncQueueDaoModule = module {
    single<SyncTaskDao> { get<SyncQueueDatabase>().syncTaskDao() }
    single<CollectTaskMetadataDao> { get<SyncQueueDatabase>().collectTaskMetadataDao() }
}

// Repository module
val syncQueueDataModule = module {
    singleOf(::SyncRepositoryImpl) { bind<SyncRepository>() }
}