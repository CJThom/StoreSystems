package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.domain.model.User
import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginRepository

class GetCurrentUserUseCase(
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(): DataResult<User> {
        return loginRepository.getCurrentUser()
    }

}
