package com.gpcasiapac.storesystems.core.identity.impl.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.impl.IdentityServiceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val identityImplModule = module {
    singleOf(::IdentityServiceImpl) { bind<IdentityService>() }
}

object IdentityImplModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(identityImplModule)
}
