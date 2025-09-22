package com.gpcasiapac.storesystems.common.networking.json

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * JVM implementation - loads JSON from classpath resources
 */
actual suspend fun loadJsonResourceString(resourcePath: String): String = withContext(Dispatchers.IO) {
    try {
        val classLoader = object {}.javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(resourcePath)
            ?: throw FileNotFoundException("Resource not found: $resourcePath")

        inputStream.bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        throw IllegalArgumentException("Failed to load resource: $resourcePath - ${e.message}", e)
    }
}
