package com.gpcasiapac.storesystems.app.collect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Collect App",
    ) {
        App()
    }
}
