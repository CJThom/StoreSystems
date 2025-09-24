package com.gpcasiapac.storesystems.app.superapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gpcasiapac.storesystems.app.superapp.navigation.SuperAppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSuperAppKoin()
        setContent { SuperAppNavigation() }
    }
}
