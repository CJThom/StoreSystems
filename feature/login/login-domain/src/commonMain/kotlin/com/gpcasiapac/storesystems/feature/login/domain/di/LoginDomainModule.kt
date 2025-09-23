package com.gpcasiapac.storesystems.feature.login.domain.di

import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.domain.service.LoginServiceImpl
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val loginDomainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::LoginServiceImpl) { bind<LoginService>() }
}