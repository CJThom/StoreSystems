import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
    }
    jvm()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlinx.coroutines.FlowPreview")
        }
        androidMain.dependencies {
            // Navigation3 UI on Android only

        }
        commonMain.dependencies {
            // Compose UI
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
      //      implementation(libs.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.uiTooling)
           // implementation(compose.uiTooling)
            //implementation(compose.uiTooling)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)

            //implementation(libs.jetbrains.compose.material3.adaptive.navigation.suite)


            // MVI base and navigation contracts
            implementation(projects.common.presentation)
            implementation(projects.common.di)

            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)

            implementation(libs.jetbrains.lifecycle.viewmodel.navigation3)
            implementation(libs.jetbrains.compose.material3.adaptive)
            implementation(libs.jetbrains.compose.material3.adaptive.navigation)
            implementation(libs.jetbrains.compose.material3.adaptive.navigation3)


            // Koin and coroutines
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // Lifecycle for ViewModel + viewModelScope
            implementation(libs.jetbrains.lifecycle.viewmodel)
            implementation(libs.jetbrains.lifecycle.runtime)

            // Logging
            implementation(libs.kermit)

            implementation(projects.core.identity.identityApi)
            // Feature API (presentation now only provides screens/viewmodels; FeatureEntry moved to collect-impl)
            implementation(projects.feature.collect.collectApi)
            implementation(projects.feature.collect.collectDomain)

            implementation(projects.common.kotlin)
            implementation(projects.foundation.designSystem)
            implementation(projects.foundation.component)
            implementation(projects.common.scanning)

            // Images (Coil KMP)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }

    }
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.collect.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
