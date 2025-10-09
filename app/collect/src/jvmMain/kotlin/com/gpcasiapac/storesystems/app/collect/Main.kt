package com.gpcasiapac.storesystems.app.collect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.gpcasiapac.storesystems.app.collect.di.appModule
import com.gpcasiapac.storesystems.app.collect.di.collectAppNavigationModule
import com.gpcasiapac.storesystems.app.collect.navigation.hostpattern.AndroidAppNavigation
import com.gpcasiapac.storesystems.core.identity.data.di.identityDataModule
import com.gpcasiapac.storesystems.core.identity.domain.di.identityDomainModule
import com.gpcasiapac.storesystems.feature.collect.data.di.collectDataModuleList
import com.gpcasiapac.storesystems.feature.collect.domain.di.collectDomainModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectFeatureModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationModule
import com.gpcasiapac.storesystems.feature.login.domain.di.loginDomainModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginFeatureModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationModule
import org.koin.core.context.startKoin

//fun main() = application {
//    Window(onCloseRequest = ::exitApplication, title = "Collect App") {
//
//            startKoin {
//                allowOverride(true)
//
//
//                modules(collectDataModuleList)
//
//                modules(
//                    loginFeatureModule,
//                    collectFeatureModule,
//                    identityDomainModule,
//                    identityDataModule,
//                    loginDomainModule,
//                    loginPresentationModule,
//                    collectPresentationModule,
//                    collectDomainModule,
//                    // collectDataModule,
//                    //  collectDataModuleList,
//                    //  collectDataAndroidModule,
//                    collectAppNavigationModule,
//                    appModule,
//                )
//            }
//
//        GPCTheme {
//            // AndroidAppNavigationGlobal()
//            AndroidAppNavigation()
//        }
//    }
//}
//

fun main() {

    startKoin {
        //   allowOverride(true)


        modules(collectDataModuleList)

        modules(
            loginFeatureModule,
            collectFeatureModule,
            identityDomainModule,
            identityDataModule,
            loginDomainModule,
            loginPresentationModule,
            collectPresentationModule,
            collectDomainModule,
            // collectDataModule,
            //  collectDataModuleList,
            //  collectDataAndroidModule,
            collectAppNavigationModule,
            appModule,
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Collect App",
        ) {
            AndroidAppNavigation()
        }
    }

}