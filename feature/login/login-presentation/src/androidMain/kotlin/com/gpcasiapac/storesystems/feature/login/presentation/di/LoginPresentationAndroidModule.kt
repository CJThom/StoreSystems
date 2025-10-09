package com.gpcasiapac.storesystems.feature.login.presentation.di

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.presentation.entry.LoginFeatureEntryAndroidImpl
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Android-specific Koin module for the Login presentation layer.
 * - Registers ViewModels so they can be obtained via koinViewModel()
 */
val loginPresentationAndroidModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::LoginNavigationViewModel)

    // Bind Android-specific FeatureEntry
    singleOf(::LoginFeatureEntryAndroidImpl).bind<LoginFeatureEntry>()


}

// TODO: Move this to common/foundation?
val loggingModule = module {
    single<Logger> { Logger.withTag("StoreSystemsLogger") }
}