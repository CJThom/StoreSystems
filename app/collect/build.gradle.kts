import org.gradle.kotlin.dsl.withType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}


kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }
    jvm()

    jvmToolchain(21)

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.preview)
            implementation(compose.components.resources)
            implementation(compose.animation)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3AdaptiveNavigationSuite)

            implementation(libs.androidx.compose.material3.adaptive.layout)
            implementation(libs.androidx.compose.material3.adaptive.navigation)
            implementation(libs.androidx.compose.material3.adaptive.navigation3)

            implementation(libs.androidx.compose.material3.adaptive)
            implementation(libs.androidx.navigation3.ui)
            implementation(libs.androidx.navigation3.runtime)
            implementation(libs.kotlinx.serialization.json)

            implementation(projects.common.presentation)
            implementation(projects.foundation.config)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.androidx.lifecycle.viewmodel.navigation3)

            // Feature dependencies
            implementation(projects.feature.login.loginApi)
            implementation(projects.feature.login.loginPresentation)
            implementation(projects.feature.history.historyPresentation)
            implementation(projects.feature.collect.collectApi)
            implementation(projects.feature.collect.collectPresentation)
            implementation(projects.feature.collect.collectDomain)
            implementation(projects.feature.collect.collectData)

            // Login/Identity wiring for DI provided from domain/presentation
            implementation(projects.feature.login.loginPresentation)
            implementation(projects.feature.login.loginDomain)
            implementation(projects.core.identity.identityDomain)
            implementation(projects.core.identity.identityData)

            // Feature flags API used for simple default binding in app
            implementation(projects.common.featureFlags)

            // Design system theme
            implementation(projects.foundation.designSystem)
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.app.collect"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.gpcasiapac.storesystems.app.collect"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
}

compose.desktop {
    application {
        mainClass = "com.gpcasiapac.storesystems.app.collect.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "storesystems-collect"
            packageVersion = "1.0.0"
        }
    }
}
