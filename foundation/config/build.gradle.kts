import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildConfig)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.common.di)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.gpcasiapac.storesystems.foundation.config"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}

// Load configuration from root-level foundation/config-config
val configDir = File(rootDir, "foundation/config")
val configFile = File(configDir, "application.properties")
val properties = Properties().apply {
    if (configFile.exists()) {
        load(FileInputStream(configFile))
        logger.lifecycle("[foundation:config] Loaded config from ${configFile.absolutePath}")
    } else {
        logger.warn("[foundation:config] application.properties not found at ${configFile.absolutePath}. Using default values.")
    }
}

val passwordFile = File(configDir, "password.properties")
val secretProps = Properties().apply {
    if (passwordFile.exists()) {
        load(FileInputStream(passwordFile))
        logger.lifecycle("[foundation:config] Loaded secrets from ${passwordFile.absolutePath}")
    } else {
        logger.warn("[foundation:config] password.properties not found at ${passwordFile.absolutePath}. Using default secret values.")
    }
}

// Get environment from system property or use default
val environment = System.getProperty("environment") ?: properties.getProperty("default.environment") ?: "mock"

buildConfig {
    useKotlinOutput { internalVisibility = false }

    forClass("BuildConfig") {
        packageName("com.gpcasiapac.storesystems.foundation.config")

        // Get configuration values using environment prefix
        val buildType = properties.getProperty("build.type") ?: "debug"
        val host = properties.getProperty("${environment}.host") ?: "localhost"
        val port = properties.getProperty("${environment}.port")?.toInt() ?: 8080
        val protocol = properties.getProperty("${environment}.protocol") ?: "http"
        val useMockData = properties.getProperty("${environment}.use_mock_data")?.toBoolean() ?: false

        // Construct API_BASE_URL from components
        val apiBaseUrl = "$protocol://$host:$port/api"

        val isDebug = when (buildType) {
            "debug" -> "true"
            "release" -> "false"
            else -> throw GradleException("Invalid build type: $buildType")
        }

        buildConfigField("String", "ENVIRONMENT", "\"$environment\"")
        buildConfigField("Boolean", "DEBUG", isDebug)
        buildConfigField("String", "HOST", "\"$host\"")
        buildConfigField("Int", "PORT", port)
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("Boolean", "USE_MOCK_DATA", useMockData)

    }
}
