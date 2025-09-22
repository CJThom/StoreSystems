package com.gpcasiapac.storesystems.feature.login.domain.di

import org.koin.dsl.module
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LogoutUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.GetCurrentUserUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.IsLoggedInUseCase
import org.koin.core.module.dsl.singleOf

val loginDomainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::IsLoggedInUseCase)
}