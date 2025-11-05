package com.gpcasiapac.storesystems.core.identity.api.model

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId

/**
 * Shared identity user model exposed to all layers via identity-api.
 */
data class User(
    val username: UserId,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val lastLoginAt: Long? = null,
)
