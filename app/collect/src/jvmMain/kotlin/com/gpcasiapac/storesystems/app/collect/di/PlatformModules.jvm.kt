package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.common.feedback.sound.FakeSoundPlayer
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.common.feedback.haptic.FakeHapticPerformer
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer
import com.gpcasiapac.storesystems.common.scanning.FakeScanner
import com.gpcasiapac.storesystems.common.scanning.Scanner
import org.koin.core.module.Module
import org.koin.dsl.module

actual val collectAppPlatformModule: Module = module {
    single<Scanner> { FakeScanner() }
    single<SoundPlayer> { FakeSoundPlayer() }
    single<HapticPerformer> { FakeHapticPerformer() }
}
