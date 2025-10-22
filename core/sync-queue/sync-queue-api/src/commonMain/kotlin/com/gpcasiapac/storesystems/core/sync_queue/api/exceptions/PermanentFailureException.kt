package com.gpcasiapac.storesystems.core.sync_queue.api.exceptions

/**
 * Throw this (wrapped in Result.failure) to indicate a non-retryable error.
 * The worker will mark the task as REQUIRES_ACTION.
 */
class PermanentFailureException(
    val code: String,
    override val message: String? = null,
) : Exception(message)
