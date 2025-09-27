package com.gpcasiapac.storesystems.common.networking.resources

/**
 * Cross-platform resource reader to load text files packaged with modules.
 * On Android, resources are expected to be available as assets; on JVM, on the classpath.
 */
expect object ResourceReader {
    suspend fun readText(path: String): String

}
