package com.gpcasiapac.storesystems.app.collect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gpcasiapac.storesystems.app.collect.di.initKoin
import com.gpcasiapac.storesystems.app.collect.navigation.AndroidAppNavigation
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(this@MainActivity)
        }

        setContent {
            GPCTheme {
                AndroidAppNavigation()
            }
        }
    }
}
