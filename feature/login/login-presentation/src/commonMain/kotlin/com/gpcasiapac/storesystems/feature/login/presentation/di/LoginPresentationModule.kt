package com.gpcasiapac.storesystems.feature.login.presentation.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.presentation.entry.LoginFeatureEntryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val loginPresentationModule = module {
    singleOf(::LoginFeatureEntryImpl) { bind<LoginFeatureEntry>() }
}

object LoginPresentationModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(loginPresentationModule)
}
