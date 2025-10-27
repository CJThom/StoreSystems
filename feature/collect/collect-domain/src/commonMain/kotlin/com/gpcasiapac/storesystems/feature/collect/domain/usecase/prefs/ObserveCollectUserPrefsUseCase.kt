package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import com.gpcasiapac.storesystems.feature.collect.domain.repository.CollectUserPrefsRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectUserPrefsUseCase(
    private val repository: CollectUserPrefsRepository,
) {
    /**
     * Observe Collect user prefs for a specific user id. Temporary default mock value to ease wiring.
     */
    operator fun invoke(userId: String = "mock-user"): Flow<CollectUserPrefs> {
        return repository.observe(userId)
    }

}
