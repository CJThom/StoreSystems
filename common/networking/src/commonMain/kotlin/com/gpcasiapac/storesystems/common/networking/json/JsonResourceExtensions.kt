package com.gpcasiapac.storesystems.common.networking.json

// Platform-specific resource loading - simple expect/actual pattern
expect suspend fun loadJsonResourceString(resourcePath: String): String
