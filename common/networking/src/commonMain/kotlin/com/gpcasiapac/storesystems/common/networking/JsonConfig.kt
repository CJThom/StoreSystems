package com.gpcasiapac.storesystems.common.networking

import kotlinx.serialization.json.Json

object JsonConfig {
    val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}