package com.gpcasiapac.storesystems.app.collect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gpcasiapac.storesystems.app.collect.navigation.AndroidAppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Koin for feature entries and services
        initCollectAppKoin()
        setContent {
            AndroidAppNavigation()
        }
    }
}
