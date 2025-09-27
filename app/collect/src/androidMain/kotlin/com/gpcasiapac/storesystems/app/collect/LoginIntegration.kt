package com.gpcasiapac.storesystems.app.collect

import androidx.activity.ComponentActivity
import com.gpcasiapac.storesystems.app.collect.di.collectAppNavigationModule
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.feature_flags.FlagKey
import com.gpcasiapac.storesystems.core.identity.data.di.identityDataModule
import com.gpcasiapac.storesystems.core.identity.domain.di.identityDomainModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationAndroidModule
import com.gpcasiapac.storesystems.feature.collect.domain.di.collectDomainModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationModule
import com.gpcasiapac.storesystems.feature.collect.data.di.collectDataModule
import com.gpcasiapac.storesystems.feature.login.domain.di.loginDomainModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationAndroidModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Start Koin once for the Collect app (Android). Safe to call multiple times.
 */
fun ComponentActivity.initCollectAppKoin() {
    if (GlobalContext.getOrNull() == null) {
        val appModule = module {
            single<FeatureFlags> {
                object : FeatureFlags {
                    override fun <T> get(key: FlagKey<T>): T = key.default
                    override fun <T> observe(key: FlagKey<T>): Flow<T> = flowOf(key.default)
                }
            }
        }
        startKoin {
            // If you want Android context in Koin elsewhere, you could add androidContext(applicationContext) here
            allowOverride(true)
            modules(
                identityDomainModule,
                identityDataModule,
                loginDomainModule,
                loginPresentationModule,
                loginPresentationAndroidModule,
                collectPresentationModule,
                collectPresentationAndroidModule,
                collectDomainModule,
                collectDataModule,
                collectAppNavigationModule,
                appModule,
            )
        }
    }
}
