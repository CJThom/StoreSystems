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
            implementation(projects.feature.collect.collectApi)
            implementation(projects.feature.collect.collectPresentation)
            implementation(projects.common.presentation)
            implementation(projects.common.di)
            implementation(libs.koin.core)
            implementation(libs.androidx.navigation3.runtime)

            // Compose basics used by entry implementations
            implementation(compose.runtime)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
        androidMain.dependencies {
            // Navigation3 UI on Android only (used by Host's internal NavDisplay)
            implementation(libs.androidx.navigation3.ui)
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.collect.impl"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
