package com.gpcasiapac.storesystems.feature.collect.data.platform

import android.content.Context
import org.koin.java.KoinJavaComponent

actual object ResourceReader {
    actual suspend fun readText(path: String): String {

        // TODO: Hopefully remove if kmp supports resources better
        // Try Android assets first (files placed under src/androidMain/assets)
        try {
            val context: Context = KoinJavaComponent.get(Context::class.java)
            context.assets.open(path).use { stream ->
                return stream.bufferedReader().use { it.readText() }
            }
        } catch (ignored: Throwable) {
            // Fallback to classpath resources (useful for tests)
        }

        val stream = this::class.java.classLoader?.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return stream.bufferedReader().use { it.readText() }
    }
}
