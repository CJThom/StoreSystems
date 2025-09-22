package com.gpcasiapac.storesystems.feature.login.domain.di

import org.koin.dsl.module
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import org.koin.core.module.dsl.singleOf

val loginDomainModule = module {
    singleOf(::LoginUseCase)
}