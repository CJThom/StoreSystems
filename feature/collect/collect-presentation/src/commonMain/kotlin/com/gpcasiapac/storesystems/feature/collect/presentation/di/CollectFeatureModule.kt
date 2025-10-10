package com.gpcasiapac.storesystems.feature.collect.presentation.di

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.presentation.entry.CollectFeatureEntryAndroidImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val collectFeatureModule = module {

}

object CollectFeatureModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(collectFeatureModule)
}
