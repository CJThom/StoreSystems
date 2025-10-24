import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }
    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.preview)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.animation)
            
            implementation(compose.material3AdaptiveNavigationSuite)

            implementation(libs.androidx.compose.material3.adaptive.layout)
            implementation(libs.androidx.compose.material3.adaptive.navigation)
            implementation(libs.androidx.compose.material3.adaptive.navigation3)

            implementation(libs.androidx.compose.material3.adaptive)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.kotlinx.serialization.json)


            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)


            implementation(libs.androidx.lifecycle.viewmodel.navigation3)


            // Feature APIs and Presentations
            implementation(projects.feature.login.loginApi)
            implementation(projects.feature.login.loginDomain)
            implementation(projects.feature.login.loginPresentation)

            implementation(projects.feature.collect.collectApi)
            implementation(projects.feature.collect.collectPresentation)
            implementation(projects.feature.collect.collectDomain)
            implementation(projects.feature.collect.collectData)

            // Optional placeholders (presentations only)
            implementation(projects.feature.history.historyPresentation)
            implementation(projects.feature.picking.pickingPresentation)

            // Domain/data DI for login/identity

            implementation(projects.core.identity.identityDomain)
            implementation(projects.core.identity.identityData)

            implementation(projects.external.featureFlags.api)
            implementation(projects.external.featureFlags.data)
            implementation(projects.common.di)
            implementation(projects.common.presentation)

            // Design system theme
            implementation(projects.foundation.designSystem)
            implementation(projects.foundation.config)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.app.superapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gpcasiapac.storesystems.app.superapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

compose.desktop {
    application {
        mainClass = "com.gpcasiapac.storesystems.app.superapp.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "storesystems-super"
            packageVersion = "1.0.0"
        }
    }
}
