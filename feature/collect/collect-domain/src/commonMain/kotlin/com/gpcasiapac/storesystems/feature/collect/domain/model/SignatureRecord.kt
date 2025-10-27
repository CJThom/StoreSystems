package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant

/**
 * Signature data persisted for a Work Order.
 * Base64 image string is kept for compatibility with existing flows.
 */
data class SignatureRecord(
    val signatureBase64: String,
    val signedByName: String?,
    val signedAt: Instant,
)
