package com.gpcasiapac.storesystems.common.networking.json

import kotlinx.serialization.DeserializationStrategy
import com.gpcasiapac.storesystems.common.networking.JsonConfig

/**
 * Load and deserialize JSON resource - works like response.body<T>()
 */
suspend inline fun <reified T> loadJsonResource(resourcePath: String): T {
    val jsonContent = loadJsonResourceString(resourcePath)
    return JsonConfig.json.decodeFromString<T>(jsonContent)
}

/**
 * Load and deserialize JSON resource with explicit deserializer
 */
suspend fun <T> loadJsonResource(
    resourcePath: String,
    deserializer: DeserializationStrategy<T>
): T {
    val jsonContent = loadJsonResourceString(resourcePath)
    return JsonConfig.json.decodeFromString(deserializer, jsonContent)
}
