package com.gpcasiapac.storesystems.common.di

import org.koin.core.module.Module

/**
 * Contract for modules to expose their Koin definitions in a consistent way.
 * This keeps discovery simple and avoids hard dependencies between modules.
 */
interface ModuleProvider {
    fun modules(): List<Module>
}
