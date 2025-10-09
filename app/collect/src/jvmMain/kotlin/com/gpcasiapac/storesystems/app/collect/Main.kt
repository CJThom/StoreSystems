package com.gpcasiapac.storesystems.app.collect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gpcasiapac.storesystems.app.collect.navigation.hostpattern.AndroidAppNavigation
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Collect App") {
        GPCTheme {
            // AndroidAppNavigationGlobal()
            AndroidAppNavigation()
        }
    }
}
