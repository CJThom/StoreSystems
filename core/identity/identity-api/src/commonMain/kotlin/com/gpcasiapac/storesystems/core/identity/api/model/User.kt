package com.gpcasiapac.storesystems.core.identity.api.model

/**
 * Shared identity user model exposed to all layers via identity-api.
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val lastLoginAt: Long? = null,
)
