package com.gpcasiapac.storesystems.core.identity.api.model

/**
 * Represents an authenticated session payload containing the authenticated user
 * and the issued token. Returned by identity login operations.
 */
 data class AuthSession(
     val user: User,
     val token: Token,
 )
