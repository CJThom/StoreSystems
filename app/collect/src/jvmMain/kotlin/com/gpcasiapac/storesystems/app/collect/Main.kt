package com.gpcasiapac.storesystems.app.collect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gpcasiapac.storesystems.app.collect.di.initKoin
import com.gpcasiapac.storesystems.app.collect.navigation.AndroidAppNavigation

fun main() {

    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Collect App",
        ) {
            AndroidAppNavigation()
        }
    }
}
