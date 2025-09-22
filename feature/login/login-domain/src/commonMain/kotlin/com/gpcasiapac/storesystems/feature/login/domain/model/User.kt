package com.gpcasiapac.storesystems.feature.login.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val lastLoginAt: Long? = null
)