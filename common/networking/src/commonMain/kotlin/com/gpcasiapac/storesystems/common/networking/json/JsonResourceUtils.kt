package com.gpcasiapac.storesystems.common.networking.json

import com.gpcasiapac.storesystems.common.networking.JsonConfig
import com.gpcasiapac.storesystems.common.networking.resources.ResourceReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy

/**
 * Load and deserialize JSON resource - works like response.body<T>()
 */
suspend inline fun <reified T> loadJsonResource(resourcePath: String): T {
    val jsonContent = withContext(Dispatchers.IO) { ResourceReader.readText(resourcePath) }
    return JsonConfig.json.decodeFromString<T>(jsonContent)
}

/**
 * Load and deserialize JSON resource with explicit deserializer
 */
suspend fun <T> loadJsonResource(
    resourcePath: String,
    deserializer: DeserializationStrategy<T>
): T {
    val jsonContent = withContext(Dispatchers.IO) { ResourceReader.readText(resourcePath) }
    return JsonConfig.json.decodeFromString(deserializer, jsonContent)
}
