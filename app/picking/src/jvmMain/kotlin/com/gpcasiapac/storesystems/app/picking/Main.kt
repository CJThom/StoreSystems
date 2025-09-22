package com.gpcasiapac.storesystems.app.picking

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Picking App") {
        App()
    }
}
