package com.gpcasiapac.storesystems.common.networking.resources

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

actual object ResourceReader : KoinComponent {
    private val context: Context by inject()

    actual suspend fun readText(path: String): String {
        // Prefer Android assets – modules can contribute assets (e.g., feature/*-data)
        try {
            context.assets.open(path).use { stream ->
                return stream.bufferedReader().use { it.readText() }
            }
        } catch (_: Throwable) {
            // Fallback to classpath lookup (useful for tests)
        }
        val stream = this::class.java.classLoader?.getResourceAsStream(path)
            ?: error("Resource not found: $path")
        return stream.bufferedReader().use { it.readText() }
    }
}
