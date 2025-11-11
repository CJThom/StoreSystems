import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.uiTooling)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            

            implementation(projects.common.presentation)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.foundation.design_system"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
