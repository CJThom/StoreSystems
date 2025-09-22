package com.gpcasiapac.storesystems.feature.login.domain.repository

import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User

data class LoginResult(
    val user: User,
    val token: Token
)