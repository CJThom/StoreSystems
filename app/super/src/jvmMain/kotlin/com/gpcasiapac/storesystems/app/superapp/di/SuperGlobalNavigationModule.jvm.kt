package com.gpcasiapac.storesystems.app.superapp.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val superGlobalNavigationModule: Module = module {
    // No-op module for desktop/JVM target; ViewModel registration is Android-only
}
