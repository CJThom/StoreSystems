package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import com.gpcasiapac.storesystems.core.identity.domain.usecase.session.ObserveCurrentUserIdFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
class GetCollectSessionIdsFlowUseCase(
    private val observeCurrentUserIdFlowUseCase: ObserveCurrentUserIdFlowUseCase,
    private val observeCollectUserPrefsUseCase: ObserveCollectUserPrefsUseCase,
) {

    operator fun invoke(): Flow<CollectSessionIds> {
        val userIdFlow: Flow<String?> = observeCurrentUserIdFlowUseCase()

        val prefsFlow: Flow<CollectUserPrefs?> = userIdFlow.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                observeCollectUserPrefsUseCase(userId)
            }
        }

        return combine(userIdFlow, prefsFlow) { userId, prefs ->
            CollectSessionIds(
                userId = userId,
                workOrderId = prefs?.selectedWorkOrderId,
            )
        }
    }
}

