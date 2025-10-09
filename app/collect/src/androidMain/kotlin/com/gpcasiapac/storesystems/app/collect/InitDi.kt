package com.gpcasiapac.storesystems.app.collect

import androidx.activity.ComponentActivity
import com.gpcasiapac.storesystems.app.collect.di.appModule
import com.gpcasiapac.storesystems.app.collect.di.collectAppNavigationModule
import com.gpcasiapac.storesystems.core.identity.data.di.identityDataModule
import com.gpcasiapac.storesystems.core.identity.domain.di.identityDomainModule
import com.gpcasiapac.storesystems.feature.collect.data.di.collectDataModuleList
import com.gpcasiapac.storesystems.feature.collect.domain.di.collectDomainModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectFeatureModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationModule
import com.gpcasiapac.storesystems.feature.login.domain.di.loginDomainModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginFeatureModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

///**
// * Start Koin once for the Collect app (Android). Safe to call multiple times.
// */
//fun ComponentActivity.initCollectAppKoin() {
//    if (GlobalContext.getOrNull() == null) {
//        val appModule = module {
//            single<FeatureFlags> {
//                object : FeatureFlags {
//                    override fun <T> get(key: FlagKey<T>): T = key.default
//                    override fun <T> observe(key: FlagKey<T>): Flow<T> = flowOf(key.default)
//                }
//            }
//            // Expose Android application context for modules that need it (e.g., Room builder)
//            single<Context> { applicationContext }
//        }
//        startKoin {
//            allowOverride(true)
//
//            modules(collectDataModuleList)
//
//            modules(
//                loginFeatureModule,
//                collectFeatureModule,
//                identityDomainModule,
//                identityDataModule,
//                loginDomainModule,
//                loginPresentationModule,
//                collectPresentationModule,
//                collectDomainModule,
//               // collectDataModule,
//              //  collectDataModuleList,
//              //  collectDataAndroidModule,
//                collectAppNavigationModule,
//                appModule,
//            )
//        }
//    }
//}

//fun initAndroidCollectAppKoin() {
//    if (GlobalContext.getOrNull() == null) {
//
//            // Expose Android application context for modules that need it (e.g., Room builder)
//          //  single<Context> { applicationContext }
//        }

fun ComponentActivity.initCollectAppKoin() {
    startKoin {
        allowOverride(true)
        androidContext(this@initCollectAppKoin)
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
}
