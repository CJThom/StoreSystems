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
            implementation(projects.feature.login.loginApi)
            implementation(projects.feature.login.loginDomain)
            implementation(projects.core.identity.identityApi)
            implementation(projects.feature.login.loginPresentation)
            implementation(projects.common.kotlin)
            implementation(projects.common.di)
            implementation(projects.common.presentation)
            implementation(projects.common.featureFlags)
            implementation(libs.koin.core)
            implementation(libs.androidx.navigation3.runtime)

            // Compose basics used by entry implementations
            implementation(compose.runtime)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.login.impl"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
