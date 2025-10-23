package com.gpcasiapac.storesystems.app.picking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.config.BuildConfig
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
@Preview
fun App() {
    GPCTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Picking App (KMP + Desktop/Android)")
                Text("Env: ${BuildConfig.ENVIRONMENT}")
                Text("Base URL: ${BuildConfig.API_BASE_URL}")
                Text("Use Mock Data: ${BuildConfig.USE_MOCK_DATA}")
            }
        }
    }
}
