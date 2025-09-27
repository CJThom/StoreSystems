package com.gpcasiapac.storesystems.feature.collect.data.platform

actual object ResourceReader {
    actual suspend fun readText(path: String): String {
        val stream = this::class.java.classLoader?.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return stream.bufferedReader().use { it.readText() }
    }
}
