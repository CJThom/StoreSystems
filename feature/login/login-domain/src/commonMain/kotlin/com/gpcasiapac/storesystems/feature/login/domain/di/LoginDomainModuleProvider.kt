package com.gpcasiapac.storesystems.feature.login.domain.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import org.koin.core.module.Module

/**
 * Provider for Login Domain Koin module.
 */
object LoginDomainModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(loginDomainModule)
}
