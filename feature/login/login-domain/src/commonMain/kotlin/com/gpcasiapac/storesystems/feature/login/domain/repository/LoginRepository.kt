package com.gpcasiapac.storesystems.feature.login.domain.repository

import com.gpcasiapac.storesystems.common.kotlin.DataResult


interface LoginRepository {
    suspend fun login(username: String, password: String): DataResult<LoginResult>
}

