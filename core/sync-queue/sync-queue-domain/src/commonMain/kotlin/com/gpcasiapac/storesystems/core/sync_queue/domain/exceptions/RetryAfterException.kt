package com.gpcasiapac.storesystems.core.sync_queue.domain.exceptions

/**
 * Throw this (wrapped in Result.failure) to indicate a retryable error.
 * Optionally include a suggested delay. In v1, WorkManager cadence will handle retries.
 */
class RetryAfterException(
    val delayMs: Long,
    val reason: String? = null,
) : Exception(reason)
