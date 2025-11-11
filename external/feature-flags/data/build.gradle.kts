import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.external.featureFlags.api)
            implementation(libs.koin.core)
            implementation(projects.foundation.config)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
        
        androidMain.dependencies {
            // Android-specific dependencies can be added here
            implementation(libs.launchdarkly.android.client.sdk)

        }
        
        jvmMain.dependencies {
            // Desktop/JVM-specific dependencies can be added here
            implementation(
              libs.launchdarkly.java.server.sdk
            )
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.external.feature_flags.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}