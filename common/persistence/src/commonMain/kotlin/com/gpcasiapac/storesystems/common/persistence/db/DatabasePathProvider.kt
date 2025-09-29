package com.gpcasiapac.storesystems.common.persistence.db

/**
 * Provides a default absolute file path for a Room database file across platforms.
 */
expect object DatabasePathProvider {
    fun defaultPath(dbFileName: String): String
}
