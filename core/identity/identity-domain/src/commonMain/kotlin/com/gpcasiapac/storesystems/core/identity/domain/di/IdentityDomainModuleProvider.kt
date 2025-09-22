package com.gpcasiapac.storesystems.core.identity.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import org.koin.core.module.Module

/**
 * Provider for Identity Domain Koin module.
 */
object IdentityDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(identityDomainModule)
}
