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
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }
    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.androidx.compose)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.ui)
            implementation(compose.preview)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.kotlinx.serialization.json)

            // Bring Lifecycle KMP into this module explicitly so commonMain VMs resolve ViewModel
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)

            implementation(projects.common.presentation)
            implementation(projects.foundation.config)
            implementation(projects.common.featureFlags)

            // Koin core for common expect declarations
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            // Feature APIs and Presentations
            implementation(projects.feature.login.loginApi)
            implementation(projects.feature.login.loginPresentation)

            implementation(projects.feature.collect.collectApi)
            implementation(projects.feature.collect.collectPresentation)
            implementation(projects.feature.collect.collectDomain)
            implementation(projects.feature.collect.collectData)

            // Optional placeholders (presentations only)
            implementation(projects.feature.history.historyPresentation)
            implementation(projects.feature.picking.pickingPresentation)

            // Domain/data DI for login/identity
            implementation(projects.feature.login.loginDomain)
            implementation(projects.core.identity.identityDomain)
            implementation(projects.core.identity.identityData)
            
            // Design system theme
            implementation(projects.foundation.designSystem)
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
    buildTypes { getByName("release") { isMinifyEnabled = false } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

dependencies {
    debugImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
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
