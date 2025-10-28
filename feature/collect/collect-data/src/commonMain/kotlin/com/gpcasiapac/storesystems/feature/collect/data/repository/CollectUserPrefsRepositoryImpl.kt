package com.gpcasiapac.storesystems.feature.collect.data.repository

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectUserPrefsDao
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectUserPrefsRepositoryImpl(
    private val dao: CollectUserPrefsDao,
) : CollectUserPrefsRepository {

    override fun observe(userId: UserId): Flow<CollectUserPrefs?> {
        return dao.observe(userId).map { it?.toDomain() }
    }

    override suspend fun get(userId: UserId): CollectUserPrefs? {
        return dao.get(userId)?.toDomain()
    }

    override suspend fun save(collectUserPrefs: CollectUserPrefs) {
        dao.upsert(collectUserPrefs.toEntity())
    }

}
