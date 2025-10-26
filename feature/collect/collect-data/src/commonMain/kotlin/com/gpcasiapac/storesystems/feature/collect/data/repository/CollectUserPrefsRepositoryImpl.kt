package com.gpcasiapac.storesystems.feature.collect.data.repository

import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectUserPrefsDao
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectUserPrefsRepositoryImpl(
    private val dao: CollectUserPrefsDao,
) : CollectUserPrefsRepository {

    override fun observe(userId: String): Flow<CollectUserPrefs> =
        dao.observe(userId).map { it?.toDomain() ?: CollectUserPrefs.DEFAULT }

    override suspend fun get(userId: String): CollectUserPrefs =
        dao.get(userId)?.toDomain() ?: CollectUserPrefs.DEFAULT

    override suspend fun save(userId: String, prefs: CollectUserPrefs) {
        dao.upsert(prefs.toEntity(userId))
    }

    override suspend fun setSelectedWorkOrderId(userId: String, id: String) {
        val updated = dao.setSelectedWorkOrderId(userId, id)
        if (updated == 0) dao.upsert(get(userId).copy(selectedWorkOrderId = id).toEntity(userId))
    }

    override suspend fun setB2BFilterSelected(userId: String, selected: Boolean) {
        val updated = dao.setB2BFilterSelected(userId, selected)
        if (updated == 0) dao.upsert(get(userId).copy(isB2BFilterSelected = selected).toEntity(userId))
    }

    override suspend fun setB2CFilterSelected(userId: String, selected: Boolean) {
        val updated = dao.setB2CFilterSelected(userId, selected)
        if (updated == 0) dao.upsert(get(userId).copy(isB2CFilterSelected = selected).toEntity(userId))
    }

    override suspend fun setSort(userId: String, sort: SortOption) {
        val updated = dao.setSort(userId, sort)
        if (updated == 0) dao.upsert(get(userId).copy(sort = sort).toEntity(userId))
    }
}
