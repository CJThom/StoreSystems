package com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectUserPrefs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


@OptIn(ExperimentalCoroutinesApi::class)
class GetCollectSessionIdsFlowUseCase(
    private val identityService: IdentityService,
    private val observeCollectUserPrefsUseCase: ObserveCollectUserPrefsUseCase,
    logger: Logger
) {

    private val log = logger.withTag("GetCollectSessionIdsFlowUseCase")

    operator fun invoke(): Flow<CollectSessionIds> {
        val userIdFlow: Flow<UserId?> = identityService.observeCurrentUserId()

        val prefsFlow: Flow<CollectUserPrefs?> = userIdFlow.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                observeCollectUserPrefsUseCase(userId)
            }
        }

        return combine(userIdFlow, prefsFlow) { userId, prefs ->
            log.d { "User ID: $userId, Work Order ID: ${prefs?.selectedWorkOrderId}"}

            CollectSessionIds(
                userId = userId,
                workOrderId = prefs?.selectedWorkOrderId,
            )
        }
    }
}

