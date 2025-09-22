package com.gpcasiapac.storesystems.feature.login.domain.model

data class Token(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val issuedAt: Long,
    val scope: String? = null
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > (issuedAt + expiresIn * 1000)
}