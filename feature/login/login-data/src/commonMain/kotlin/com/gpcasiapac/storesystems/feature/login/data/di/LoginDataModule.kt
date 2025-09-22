package com.gpcasiapac.storesystems.feature.login.data.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.login.data.network.source.LoginNetworkDataSource
import com.gpcasiapac.storesystems.feature.login.data.network.source.MockLoginNetworkDataSourceImpl
import com.gpcasiapac.storesystems.feature.login.data.repository.LoginRepositoryImpl
import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val loginDataModule = module {
    singleOf(::MockLoginNetworkDataSourceImpl) { bind<LoginNetworkDataSource>() }
    singleOf(::LoginRepositoryImpl) { bind<LoginRepository>() }
}

object LoginDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(loginDataModule)
}
