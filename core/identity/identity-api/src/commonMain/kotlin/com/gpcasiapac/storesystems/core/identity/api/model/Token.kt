package com.gpcasiapac.storesystems.core.identity.api.model

/**
 * Shared auth token model exposed via identity-api.
 */
data class Token(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val issuedAt: Long,
    val scope: String? = null,
)
