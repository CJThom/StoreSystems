package com.gpcasiapac.storesystems.app.collect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gpcasiapac.storesystems.app.collect.navigation.hostpattern.AndroidAppNavigation
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize Koin for feature entries and services
        initCollectAppKoin()

        setContent {
            GPCTheme {
           //     KoinContext {
                    // AndroidAppNavigationGlobal()
                    AndroidAppNavigation()
            //    }
            }
        }
    }
}

