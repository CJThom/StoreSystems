package com.gpcasiapac.storesystems.common.networking.resources

import co.touchlab.kermit.Logger

actual object ResourceReader {
    actual suspend fun readText(path: String): String {
        val tag = "ResourceReader"
        val cl = this::class.java.classLoader ?: ClassLoader.getSystemClassLoader()
        Logger.d(tag) { "JVM: trying to read classpath resource '${'$'}path'" }
        val stream = cl?.getResourceAsStream(path)
            ?: error("Resource not found: ${'$'}path")
        val text = stream.bufferedReader().use { it.readText() }
        Logger.d(tag) { "JVM: read ${'$'}{text.length} chars from '${'$'}path'" }
        return text
    }
}
