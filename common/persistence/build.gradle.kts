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
            // Room runtime APIs and bundled SQLite driver for non-Android targets
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            // Android driver for Room KMP
            implementation(libs.androidx.sqlite.android)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.common.persistence"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
