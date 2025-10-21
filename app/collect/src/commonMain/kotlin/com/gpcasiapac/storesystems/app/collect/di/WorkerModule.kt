package com.gpcasiapac.storesystems.app.collect.di

import org.koin.core.module.Module

// Platform-specific worker modules. On non-Android targets, this is empty.
expect fun workerModules(): List<Module>
