import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    jvm()
    sourceSets {
        commonMain.dependencies {
            // Ktor client core and JSON content negotiation
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.resources)

            // kotlinx.serialization JSON (used directly by JsonConfig)
            implementation(libs.kotlinx.serialization.json)

            // Logging
            implementation(libs.kermit)

            // DI
            implementation(libs.koin.core)

            // Config for base URL, host, port
            implementation(projects.foundation.config)
        }
        androidMain.dependencies {
            // Android engine
            implementation(libs.ktor.client.okhttp)
        }
        jvmMain.dependencies {
            // Desktop/JVM engine
            implementation(libs.ktor.client.cio)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.common.networking"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
