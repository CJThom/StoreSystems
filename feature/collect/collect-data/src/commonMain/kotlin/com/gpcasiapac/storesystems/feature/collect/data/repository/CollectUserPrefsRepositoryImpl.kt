package com.gpcasiapac.storesystems.feature.collect.data.repository

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.data.local.db.dao.CollectUserPrefsDao
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectUserPrefsRepositoryImpl(
    private val collectUserPrefsDao: CollectUserPrefsDao,
) : CollectUserPrefsRepository {

    override fun observe(userId: UserId): Flow<CollectUserPrefs?> {
        return collectUserPrefsDao.observe(userId).map { it?.toDomain() }
    }

    override suspend fun get(userId: UserId): CollectUserPrefs? {
        return collectUserPrefsDao.get(userId)?.toDomain()
    }

    override suspend fun save(collectUserPrefs: CollectUserPrefs) {
        collectUserPrefsDao.upsert(collectUserPrefs.toEntity())
    }

    override suspend fun setSelectedWorkOrderId(userId: UserId, selectedWorkOrderId: WorkOrderId?): Int {
        return collectUserPrefsDao.setSelectedWorkOrderId(userId, selectedWorkOrderId)
    }

}
