package com.gpcasiapac.storesystems.common.networking.resources

import android.content.Context
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual object ResourceReader : KoinComponent {
    private val context: Context by inject()

    actual suspend fun readText(path: String): String {
        val tag = "ResourceReader"
        // Prefer Android assets – modules can contribute assets (e.g., feature/*-data)
        try {
            Logger.d(tag) { "Android: trying to read asset '${'$'}path'" }
            context.assets.open(path).use { stream ->
                val text = stream.bufferedReader().use { it.readText() }
                Logger.d(tag) { "Android: read ${'$'}{text.length} chars from asset '${'$'}path'" }
                return text
            }
        } catch (t: Throwable) {
            Logger.d(tag) { "Android: asset '${'$'}path' not found, falling back to classpath (${ '$'}{t::class.simpleName})" }
            // Fallback to classpath lookup (useful for tests)
        }
        Logger.d(tag) { "Android: trying to read classpath resource '${'$'}path'" }
        val stream = this::class.java.classLoader?.getResourceAsStream(path)
            ?: error("Resource not found: ${'$'}path")
        val text = stream.bufferedReader().use { it.readText() }
        Logger.d(tag) { "Android: read ${'$'}{text.length} chars from classpath '${'$'}path'" }
        return text
    }
}
