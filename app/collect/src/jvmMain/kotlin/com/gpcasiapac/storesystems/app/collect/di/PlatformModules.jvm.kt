package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.common.scanning.FakeScanner
import com.gpcasiapac.storesystems.common.scanning.Scanner
import org.koin.core.module.Module
import org.koin.dsl.module

actual val collectAppPlatformModule: Module = module {
    single<Scanner> { FakeScanner() }
}
