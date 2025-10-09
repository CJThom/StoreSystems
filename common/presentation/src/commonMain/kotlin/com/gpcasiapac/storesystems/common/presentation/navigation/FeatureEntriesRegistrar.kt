//package com.gpcasiapac.storesystems.common.presentation.navigation
//
//import androidx.navigation3.runtime.EntryProviderBuilder
//import androidx.navigation3.runtime.NavKey
//
///**
// * Small wrapper used by FeatureEntry implementations to register their Navigation3 entries
// * without exposing raw builder/push/pop parameters everywhere.
// */
//interface FeatureEntriesRegistrar {
//    val builder: EntryProviderBuilder<NavKey>
//    fun push(key: NavKey)
//    fun pop()
//}
//
//fun featureEntriesRegistrar(
//    builder: EntryProviderBuilder<NavKey>,
//    push: (NavKey) -> Unit,
//    pop: () -> Unit,
//): FeatureEntriesRegistrar = object : FeatureEntriesRegistrar {
//    override val builder: EntryProviderBuilder<NavKey> = builder
//    override fun push(key: NavKey) = push.invoke(key)
//    override fun pop() = pop.invoke()
//}
