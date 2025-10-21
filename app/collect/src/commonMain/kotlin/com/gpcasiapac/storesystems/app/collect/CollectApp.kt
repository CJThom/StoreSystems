package com.gpcasiapac.storesystems.app.collect

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.app.collect.di.getAppModules
import com.gpcasiapac.storesystems.app.collect.navigation.CollectNavDisplay
import com.gpcasiapac.storesystems.app.collect.scanning.ScanLifecycleHost
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CollectApp() {
    GPCTheme {
        KoinMultiplatformApplication(
            config = KoinConfiguration {
                modules(getAppModules())
            }
        ) {
            val logger: Logger = koinInject()
            logger.i { "Compose: CollectApp start" }

            CollectNavDisplay()

            // Cross-platform scanner lifecycle host
            ScanLifecycleHost()
        }
    }
}