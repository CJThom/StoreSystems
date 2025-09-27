package com.gpcasiapac.storesystems.feature.collect.data.platform

/**
 * Simple expect/actual resource reader to load text files from commonMain resources.
 * Platform-specific implementations should read from the classpath/resource bundle.
 */
expect object ResourceReader {
    suspend fun readText(path: String): String
}
