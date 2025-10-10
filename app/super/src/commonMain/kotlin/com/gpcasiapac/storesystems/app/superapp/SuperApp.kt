package com.gpcasiapac.storesystems.app.superapp

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.app.superapp.di.getAppModules
import com.gpcasiapac.storesystems.app.superapp.navigation.SuperNavDisplay
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SuperApp() {
    GPCTheme {
        KoinMultiplatformApplication(
            config = KoinConfiguration {
                modules(getAppModules())
            }
        ) {
            SuperNavDisplay()
        }
    }
}