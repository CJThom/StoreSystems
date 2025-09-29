package com.gpcasiapac.storesystems.feature.collect.data.repository

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderSelectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory implementation of [OrderSelectionRepository].
 *
 * Notes:
 * - Scoped by [userRefId] so different users (or sessions) can have independent selections.
 * - Backing store can be swapped to a persistent key-value store later without changing callers.
 */
class OrderSelectionRepositoryImpl : OrderSelectionRepository {

    private val mutex = Mutex()
    private val selectionMap: MutableMap<String, MutableStateFlow<Set<String>>> = mutableMapOf()

    private fun scopeKey(userRefId: String?): String = userRefId?.takeIf { it.isNotBlank() } ?: GLOBAL_SCOPE

    private fun getOrCreateFlow(key: String): MutableStateFlow<Set<String>> =
        selectionMap.getOrPut(key) { MutableStateFlow(emptySet()) }

    override fun getSelectedIdListFlow(userRefId: String?): Flow<Set<String>> {
        val key = scopeKey(userRefId)
        return getOrCreateFlow(key).asStateFlow()
    }

    override suspend fun setSelectedIdList(orderIdList: List<String>, userRefId: String?) {
        val key = scopeKey(userRefId)
        mutex.withLock {
            getOrCreateFlow(key).value = orderIdList.toSet()
        }
    }

    override suspend fun addSelectedId(orderId: String, userRefId: String?) {
        val key = scopeKey(userRefId)
        mutex.withLock {
            val flow = getOrCreateFlow(key)
            flow.value = flow.value.toMutableSet().apply { add(orderId) }
        }
    }

    override suspend fun removeSelectedId(orderId: String, userRefId: String?) {
        val key = scopeKey(userRefId)
        mutex.withLock {
            val flow = getOrCreateFlow(key)
            flow.value = flow.value.toMutableSet().apply { remove(orderId) }
        }
    }

    override suspend fun clear(userRefId: String?) {
        val key = scopeKey(userRefId)
        mutex.withLock {
            getOrCreateFlow(key).value = emptySet()
        }
    }

    private companion object {
        const val GLOBAL_SCOPE = "global"
    }
}
