package com.gpcasiapac.storesystems.app.collect

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.app.collect.di.getAppModules
import com.gpcasiapac.storesystems.app.collect.navigation.CollectNavDisplay
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.koin.compose.KoinMultiplatformApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration


@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    GPCTheme {
        KoinMultiplatformApplication(
            config = KoinConfiguration {
                modules(getAppModules())
            }
        ) {
            CollectNavDisplay()
        }
    }
}