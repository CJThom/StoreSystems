package com.gpcasiapac.storesystems.common.networking.json

import com.gpcasiapac.storesystems.common.networking.JsonConfig
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

/**
 * Shared JSON utilities for KMP networking/data modules.
 * - Provides a single default Json configuration.
 * - Generic decode/encode helpers (reified and strategy-based).
 */
object JsonUtil {

    /**
     * Default Json instance used across modules.
     * Delegates to the single source of truth in JsonConfig to keep
     * HTTP (Ktor ContentNegotiation) and manual JSON parsing consistent.
     */
    val default: Json = JsonConfig.json

    /** Generic decode using reified type parameter. */
    inline fun <reified T> decodeFromString(text: String, json: Json = default): T {
        return json.decodeFromString(text)
    }

    /** Generic decode using an explicit deserialization strategy. */
    fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, text: String, json: Json = default): T {
        return json.decodeFromString(deserializer, text)
    }

    /** Generic encode using reified type parameter. */
    inline fun <reified T> encodeToString(value: T, json: Json = default): String {
        return json.encodeToString(value)
    }

    /** Generic encode using an explicit serialization strategy. */
    fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T, json: Json = default): String {
        return json.encodeToString(serializer, value)
    }

}
