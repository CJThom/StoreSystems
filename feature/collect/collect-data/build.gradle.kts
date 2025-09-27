import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_11) } }
    jvm()
    sourceSets {
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlinx.coroutines.FlowPreview")
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.feature.collect.collectDomain)
            implementation(projects.feature.collect.collectApi)

            implementation(libs.androidx.room.runtime)
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.collect.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
