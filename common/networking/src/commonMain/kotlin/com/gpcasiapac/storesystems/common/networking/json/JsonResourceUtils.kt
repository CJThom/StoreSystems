package com.gpcasiapac.storesystems.common.networking.json

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.networking.JsonConfig
import com.gpcasiapac.storesystems.common.networking.resources.ResourceReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy

/**
 * Load and deserialize JSON resource - works like response.body<T>()
 */
suspend inline fun <reified T> loadJsonResource(resourcePath: String): T {
    val tag = "JsonResource"
    Logger.d(tag) { "loadJsonResource<${'T'}>(path=$resourcePath) start" }
    try {
        val jsonContentRaw = withContext(Dispatchers.IO) { ResourceReader.readText(resourcePath) }
        val jsonContent = jsonContentRaw.removeUtf8Bom()
        Logger.d(tag) { "loadJsonResource: read ${jsonContent.length} chars from $resourcePath (bomRemoved=${jsonContentRaw !== jsonContent})" }
        return JsonConfig.json.decodeFromString<T>(jsonContent).also {
            Logger.d(tag) { "loadJsonResource: decode success" }
        }
    } catch (t: Throwable) {
        Logger.e(tag, t) { "loadJsonResource: failed for $resourcePath" }
        throw t
    }
}

/**
 * Load and deserialize JSON resource with explicit deserializer
 */
suspend fun <T> loadJsonResource(
    resourcePath: String,
    deserializer: DeserializationStrategy<T>
): T {
    val tag = "JsonResource"
    Logger.d(tag) { "loadJsonResource(path=$resourcePath) start (explicit deserializer)" }
    try {
        val jsonContentRaw = withContext(Dispatchers.IO) { ResourceReader.readText(resourcePath) }
        val jsonContent = jsonContentRaw.removeUtf8Bom()
        Logger.d(tag) { "loadJsonResource: read ${jsonContent.length} chars from $resourcePath (bomRemoved=${jsonContentRaw !== jsonContent})" }
        return JsonConfig.json.decodeFromString(deserializer, jsonContent).also {
            Logger.d(tag) { "loadJsonResource: decode success (explicit)" }
        }
    } catch (t: Throwable) {
        Logger.e(tag, t) { "loadJsonResource: failed for $resourcePath (explicit)" }
        throw t
    }
}


@PublishedApi
internal fun String.removeUtf8Bom(): String = if (isNotEmpty() && this[0] == '\uFEFF') substring(1) else this
