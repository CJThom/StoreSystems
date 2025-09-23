package com.gpcasiapac.storesystems.feature.login.presentation.di

import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.presentation.entry.LoginFeatureEntryAndroidImpl
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Android-specific Koin module for the Login presentation layer.
 * - Registers ViewModels so they can be obtained via koinViewModel()
 * - Overrides the common LoginFeatureEntry with the Android implementation
 */
val loginPresentationAndroidModule = module {
    // ViewModels (retrieved via org.koin.compose.viewmodel.koinViewModel)
    viewModel { LoginViewModel(get(), get()) }
    viewModel { LoginNavViewModel() }

    // Bind Android-specific FeatureEntry; override is enabled at startKoin
    singleOf(::LoginFeatureEntryAndroidImpl) { bind<LoginFeatureEntry>() }
}
