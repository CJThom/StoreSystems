package com.gpcasiapac.storesystems.core.sync_queue.domain
import org.koin.core.module.Module

// Platform-specific worker modules. On non-Android targets, this is empty.
expect fun workerModules(): List<Module>
