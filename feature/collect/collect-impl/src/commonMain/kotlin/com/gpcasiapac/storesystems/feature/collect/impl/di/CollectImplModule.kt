package com.gpcasiapac.storesystems.feature.collect.impl.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.impl.entry.CollectOrdersFeatureEntryImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val collectImplModule = module {
    // FeatureEntry binding (common implementation by default)
    singleOf(::CollectOrdersFeatureEntryImpl) { bind<CollectOrdersFeatureEntry>() }
}

object CollectImplModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectImplModule)
}
