package com.gpcasiapac.storesystems.core.sync_queue.domain.registry

import com.gpcasiapac.storesystems.core.sync_queue.domain.SyncHandler
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.TaskType

/**
 * Builds a dispatch map from a list of handlers and detects duplicates at init.
 */
class SyncHandlerRegistry(handlers: List<SyncHandler>) {
    private val map: Map<TaskType, SyncHandler>

    init {
        val pairs = handlers.flatMap { h -> h.supportedTypes.map { it to h } }
        val grouped = pairs.groupBy({ it.first }, { it.second })
        val conflicts = grouped.filterValues { it.toSet().size > 1 }
        require(conflicts.isEmpty()) { "Duplicate SyncHandler(s) for: ${'$'}{conflicts.keys}" }
        map = pairs.associate { it.first to it.second }
    }

    fun get(type: TaskType): SyncHandler? = map[type]
}
