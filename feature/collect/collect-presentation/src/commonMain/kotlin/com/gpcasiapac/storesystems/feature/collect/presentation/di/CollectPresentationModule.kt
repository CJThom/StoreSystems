package com.gpcasiapac.storesystems.feature.collect.presentation.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.presentation.entry.CollectOrdersFeatureEntryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val collectPresentationModule = module {
    singleOf(::CollectOrdersFeatureEntryImpl) { bind<CollectOrdersFeatureEntry>() }
}

object CollectPresentationModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectPresentationModule)
}
