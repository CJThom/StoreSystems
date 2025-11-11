import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.identity.identityApi)
            implementation(projects.core.identity.identityDomain)
            implementation(projects.common.kotlin)
            implementation(projects.common.di)
            implementation(projects.common.networking)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.kermit)
            implementation(libs.androidx.datastore.preferencesCore)

            // Room KMP runtime and SQLite driver
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }
        androidMain.dependencies {
            // Optional: wrapper for SupportSQLite APIs
            implementation(libs.androidx.room.sqlite.wrapper)
        }
        val jvmMain by getting {
            // Ensure commonMain/resources are on the JVM classpath for this module (if needed later)
            resources.srcDirs("src/commonMain/resources")
        }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.core.identity.data"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }

    // Package commonMain/resources into Android assets so ResourceReader can load them
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/commonMain/resources")
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
