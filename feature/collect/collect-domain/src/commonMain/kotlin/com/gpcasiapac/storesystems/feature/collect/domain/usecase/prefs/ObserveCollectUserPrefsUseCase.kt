package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectUserPrefsUseCase(
    private val repository: CollectUserPrefsRepository,
) {
    /**
     * Observe Collect user prefs for a specific user id. Temporary default mock value to ease wiring.
     */
    operator fun invoke(userId: UserId = UserId("demo")): Flow<CollectUserPrefs?> {
        return repository.observe(userId)
    }

}
