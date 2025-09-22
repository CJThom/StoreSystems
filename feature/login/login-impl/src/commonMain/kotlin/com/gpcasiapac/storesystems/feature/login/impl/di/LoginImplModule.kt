package com.gpcasiapac.storesystems.feature.login.impl.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.impl.entry.LoginFeatureEntryImpl
import com.gpcasiapac.storesystems.feature.login.impl.service.LoginServiceImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val loginImplModule = module {
    // Service facade implementation
    singleOf(::LoginServiceImpl) { bind<LoginService>() }

    // FeatureEntry (common default; Android-specific variant can override in platform module if desired)
    single<LoginFeatureEntry> { LoginFeatureEntryImpl() }
}

object LoginImplModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(loginImplModule)
}
