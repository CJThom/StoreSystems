package com.gpcasiapac.storesystems.core.identity.domain.di

import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.domain.service.IdentityServiceImpl
import com.gpcasiapac.storesystems.core.identity.domain.usecase.GetCurrentUserUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.IsLoggedInUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LogoutUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.RefreshTokenUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val identityDomainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::IsLoggedInUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::RefreshTokenUseCase)

    // Bind public IdentityService facade here (impl lives in domain now)
    singleOf(::IdentityServiceImpl) { bind<IdentityService>() }
}
