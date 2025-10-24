package com.gpcasiapac.storesystems.feature.login.domain.di

import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.domain.service.LoginServiceImpl
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.UpdateFeatureFlagContextUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.CheckMfaRequirementUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.ClearFeatureFlagContextUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val loginDomainModule = module {
    // Feature flag use cases
    singleOf(::UpdateFeatureFlagContextUseCase)
    singleOf(::CheckMfaRequirementUseCase)
    singleOf(::ClearFeatureFlagContextUseCase)
    
    // Main use case
    singleOf(::LoginUseCase)
    
    // Service
    singleOf(::LoginServiceImpl) { bind<LoginService>() }
}