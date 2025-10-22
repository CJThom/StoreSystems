package com.gpcasiapac.storesystems.app.collect.di

import android.content.Context
import android.media.AudioManager
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.common.feedback.sound.AndroidToneSoundPlayer
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer
import com.gpcasiapac.storesystems.common.feedback.haptic.AndroidHapticPerformer
import com.gpcasiapac.storesystems.common.scanning.Scanner
import com.gpcasiapac.storesystems.common.scanning.android.DataWedgeScanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val collectAppPlatformModule: Module = module {
    // Scanner binding (Zebra DataWedge)
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

    // Sound feedback binding
    single<SoundPlayer> {
        val logger: Logger = get()
        AndroidToneSoundPlayer(
            logger = logger,
            streamType = AudioManager.STREAM_NOTIFICATION,
            volumePercent = 100
        )
    }

    // Haptic feedback binding
    single<HapticPerformer> {
        val logger: Logger = get()
        AndroidHapticPerformer(
            context = androidContext().applicationContext,
            logger = logger
        )
    }
}
