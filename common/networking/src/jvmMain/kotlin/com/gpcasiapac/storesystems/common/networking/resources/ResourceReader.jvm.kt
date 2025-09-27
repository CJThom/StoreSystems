package com.gpcasiapac.storesystems.common.networking.resources

actual object ResourceReader {
    actual suspend fun readText(path: String): String {

        val cl = this::class.java.classLoader ?: ClassLoader.getSystemClassLoader()
        val stream = cl?.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return stream.bufferedReader().use { it.readText() }
    }
}
