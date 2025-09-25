package com.gpcasiapac.storesystems.app.superapp

import androidx.activity.ComponentActivity
import com.gpcasiapac.storesystems.app.superapp.di.superGlobalNavigationModule
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.feature_flags.FlagKey
import com.gpcasiapac.storesystems.core.identity.data.di.identityDataModule
import com.gpcasiapac.storesystems.core.identity.domain.di.identityDomainModule
import com.gpcasiapac.storesystems.feature.login.domain.di.loginDomainModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationAndroidModule
import com.gpcasiapac.storesystems.feature.login.presentation.di.loginPresentationModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationModule
import com.gpcasiapac.storesystems.feature.collect.presentation.di.collectPresentationAndroidModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/** Start Koin once for the Super app (Android). Safe to call multiple times. */
fun ComponentActivity.initSuperAppKoin() {
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
            allowOverride(true)
            modules(
                identityDomainModule,
                identityDataModule,
                loginDomainModule,
                loginPresentationModule,
                loginPresentationAndroidModule,
                collectPresentationModule,
                collectPresentationAndroidModule,
                superGlobalNavigationModule,
                com.gpcasiapac.storesystems.app.superapp.di.superAppHostModule,
                appModule,
            )
        }
    }
}
