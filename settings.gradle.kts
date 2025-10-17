rootProject.name = "StoreSystems"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// App modules
include(":app:picking")
include(":app:collect")
include(":app:super")

// Common modules
include(":common:kotlin")
include(":common:di")
include(":common:presentation")
include(":common:networking")
include(":common:persistence")
include(":common:telemetry")
include(":common:scanning")

// External modules
include(":external:feature-flags:api")
include(":external:feature-flags:data")

// Foundation modules
include(":foundation:design-system")
include(":foundation:component")
include(":foundation:config")

// Core modules
include(":core:identity:identity-api")
include(":core:identity:identity-domain")
include(":core:identity:identity-data")
include(":core:sync-queue:sync-queue-api")
include(":core:sync-queue:sync-queue-domain")
include(":core:sync-queue:sync-queue-data")
include(":core:sync-queue:sync-queue-presentation")

// Feature modules
include(":feature:login:login-api")
include(":feature:login:login-domain")
//include(":feature:login:login-data")
include(":feature:login:login-presentation")

include(":feature:history:history-api")
include(":feature:history:history-domain")
include(":feature:history:history-data")
include(":feature:history:history-presentation")

include(":feature:picking:picking-api")
include(":feature:picking:picking-domain")
include(":feature:picking:picking-data")
include(":feature:picking:picking-presentation")

include(":feature:collect:collect-api")
include(":feature:collect:collect-domain")
include(":feature:collect:collect-data")
include(":feature:collect:collect-presentation")