import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    jvm()
    sourceSets {
        commonMain.dependencies {
            // Compose UI
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            // MVI base and navigation contracts
            implementation(projects.common.presentation)
            implementation(projects.common.di)
            implementation(libs.androidx.navigation3.runtime)

            // Koin and coroutines
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)

            // Lifecycle for ViewModel + viewModelScope
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)

            // Feature API (presentation now only provides screens/viewmodels; FeatureEntry moved to collect-impl)
            implementation(projects.feature.collect.collectApi)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
        androidMain.dependencies {
            // Navigation3 UI on Android only
            implementation(libs.androidx.navigation3.ui)
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.collect.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
