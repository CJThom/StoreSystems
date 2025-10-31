package com.gpcasiapac.storesystems.feature.collect.data.time

import kotlin.time.Instant


/**
 * Parses an ISO-8601 instant string (e.g., 2025-09-29T07:35:00Z) into kotlin.time.Instant.
 * Uses kotlinx-datetime for cross-platform parsing and converts to Kotlin stdlib Instant via epoch millis.
 */
fun String?.toKotlinInstantOrNull(): Instant? {
    if (this.isNullOrBlank()) return null
    return try {
        val epochMs = Instant.parse(this).toEpochMilliseconds()
        Instant.fromEpochMilliseconds(epochMs)
    } catch (_: Throwable) {
        null
    }
}

/**
 * Parses an ISO-8601 instant string into kotlin.time.Instant, falling back to epoch 0 on failure.
 */
fun String?.toKotlinInstantOrEpoch0(): Instant {
    return toKotlinInstantOrNull() ?: Instant.fromEpochMilliseconds(0)
}
