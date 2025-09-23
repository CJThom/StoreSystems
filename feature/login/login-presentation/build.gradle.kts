import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }
    jvm()
    sourceSets {
        androidMain.dependencies {
          //  implementation(compose.preview)
          //  implementation(libs.androidx.activity.compose)
           // implementation(libs.koin.androidx.compose)

            implementation(libs.androidx.navigation3.ui)
        }
        commonMain.dependencies {
            // Compose UI
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            // MVI base and feature contracts
            implementation(projects.common.presentation)
            implementation(projects.common.featureFlags)
            implementation(projects.common.kotlin)
            implementation(projects.common.di)
            implementation(projects.feature.login.loginApi)
            implementation(projects.feature.login.loginDomain)
            implementation(projects.core.identity.identityApi)

            // Navigation3 runtime for NavKey/entry registrations
            implementation(libs.androidx.navigation3.runtime)

            // Coroutines and Lifecycle
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)

            // Koin DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.login.presentation"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
