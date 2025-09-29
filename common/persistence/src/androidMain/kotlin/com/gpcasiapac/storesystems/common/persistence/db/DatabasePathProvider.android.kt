package com.gpcasiapac.storesystems.common.persistence.db

actual object DatabasePathProvider {
    actual fun defaultPath(dbFileName: String): String {
        // Avoid DI dependencies here; use system temp directory as a safe default.
        val base = System.getProperty("java.io.tmpdir") ?: "."
        val sep = if (base.endsWith("/")) "" else "/"
        return base + sep + dbFileName
    }
}
