package com.gpcasiapac.storesystems.common.persistence.db

import java.io.File

actual object DatabasePathProvider {
    actual fun defaultPath(dbFileName: String): String {
        val baseDir = System.getProperty("java.io.tmpdir")
        return File(baseDir, dbFileName).absolutePath
    }
}
