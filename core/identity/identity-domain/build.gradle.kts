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
            implementation(projects.common.kotlin)
            implementation(projects.common.di)
            implementation(projects.core.identity.identityApi)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.core.identity.domain"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
