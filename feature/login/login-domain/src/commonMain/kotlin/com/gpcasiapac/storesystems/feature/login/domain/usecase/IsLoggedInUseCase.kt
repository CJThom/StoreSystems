package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginRepository
import com.gpcasiapac.storesystems.common.kotlin.DataResult

class IsLoggedInUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(): Boolean {
        return loginRepository.getCurrentUser().let { result ->
            when (result) {
                is DataResult.Success -> true
                is DataResult.Error -> false
            }
        }
    }
}
