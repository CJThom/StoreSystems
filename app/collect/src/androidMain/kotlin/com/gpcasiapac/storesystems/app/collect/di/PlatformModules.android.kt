package com.gpcasiapac.storesystems.app.collect.di

import android.content.Context
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.Scanner
import com.gpcasiapac.storesystems.common.scanning.android.DataWedgeScanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.math.log

actual val collectAppPlatformModule: Module = module {
    single<Scanner> {
        val ctx: Context = androidContext().applicationContext
        val logger: Logger = get()
        logger.d { "Creating DataWedgeScanner" }
        DataWedgeScanner(
            appContext = ctx,
            logger = logger,
            profileName = "StoreSystemsProfile",
            intentAction = "com.gpcasiapac.storesystems.SCAN",
            autoCreateProfile = true
        )
    }
}
