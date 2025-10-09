package com.gpcasiapac.storesystems.feature.login.presentation.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.presentation.entry.LoginFeatureEntryAndroidImpl
import com.gpcasiapac.storesystems.feature.login.presentation.entry.LoginFeatureEntryImpl
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val loginPresentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::LoginNavigationViewModel)

    // Bind Android-specific FeatureEntry
    singleOf(::LoginFeatureEntryAndroidImpl).bind<LoginFeatureEntry>()
    //factory<LoginFeatureEntry> { LoginFeatureEntryAndroidImpl() }
  //  singleOf(::LoginFeatureEntryImpl) { bind<LoginFeatureEntry>() }
}

object LoginPresentationModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(loginPresentationModule)
}
