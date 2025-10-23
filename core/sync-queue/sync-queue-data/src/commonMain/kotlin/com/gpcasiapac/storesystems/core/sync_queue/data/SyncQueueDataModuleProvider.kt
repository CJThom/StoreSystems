package com.gpcasiapac.storesystems.core.sync_queue.data

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import org.koin.core.module.Module

object SyncQueueDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(
        syncQueueDatabaseModule,
        syncQueueDaoModule,
        syncQueueDataModule
    )
}
