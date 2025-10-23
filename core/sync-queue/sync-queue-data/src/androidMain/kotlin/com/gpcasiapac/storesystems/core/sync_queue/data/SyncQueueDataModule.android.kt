package com.gpcasiapac.storesystems.core.sync_queue.data

import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.SyncQueueDatabase
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.getSyncQueueDatabase
import com.gpcasiapac.storesystems.core.sync_queue.data.local.db.getSyncQueueDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val syncQueueDatabaseModule: Module = module {
    single<SyncQueueDatabase> { 
        getSyncQueueDatabase(getSyncQueueDatabaseBuilder(get())) 
    }
}
