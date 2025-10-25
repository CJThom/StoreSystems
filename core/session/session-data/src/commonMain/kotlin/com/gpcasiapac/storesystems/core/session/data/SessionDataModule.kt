package com.gpcasiapac.storesystems.core.session.data

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.session.api.IdentitySessionFlows
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sessionDataModule = module {
    singleOf(::IdentitySessionFlowsImpl) { bind<IdentitySessionFlows>() }
    singleOf(::AuthManager)
}

object SessionDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(sessionDataModule)
}
