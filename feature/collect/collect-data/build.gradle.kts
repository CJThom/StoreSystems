import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
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
        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlinx.coroutines.FlowPreview")
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(projects.feature.collect.collectDomain)
            implementation(projects.feature.collect.collectApi)
            implementation(projects.common.di)
            implementation(libs.koin.core)

            // Shared utilities
            implementation(projects.common.kotlin)
            implementation(projects.common.networking)
            implementation(projects.common.persistence)

            // Room KMP runtime and bundled SQLite driver
            implementation(libs.androidx.room.runtime)
           // implementation(libs.androidx.room.ktx)
            implementation(libs.androidx.sqlite.bundled)
        }
        androidMain.dependencies {
            // Optional: Room SQLite Wrapper for compatibility with SupportSQLite APIs
            implementation(libs.androidx.room.sqlite.wrapper)
            // Use OS-provided SQLite on Android via AndroidSQLiteDriver
          //  implementation(libs.androidx.sqlite.android)
        }
        val jvmMain by getting {
            // Ensure commonMain/resources are on the JVM classpath for this module
            resources.srcDirs("src/commonMain/resources")
        }
    }
}

// KSP processors for each target used by this module
dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.gpcasiapac.storesystems.feature.collect.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }

    // TODO: Hopefully remove if kmp supports resources better
    // Package commonMain/resources into Android assets so ResourceReader can load them
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/commonMain/resources")
        }
    }
}
