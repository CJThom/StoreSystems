package com.gpcasiapac.storesystems.app.superapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern.SuperAppNavigation
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSuperAppKoin()
        setContent {
            GPCTheme {
                SuperAppNavigation()
            }
           // SuperAppGlobalNavigation()
        }
    }
}
