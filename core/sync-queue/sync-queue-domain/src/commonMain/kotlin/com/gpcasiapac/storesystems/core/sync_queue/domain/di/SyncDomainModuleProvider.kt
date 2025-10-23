package com.gpcasiapac.storesystems.core.sync_queue.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.sync_queue.domain.workerModules
import org.koin.core.module.Module

object SyncDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> {
        return workerModules() + syncQueueDomainModule
    }
}