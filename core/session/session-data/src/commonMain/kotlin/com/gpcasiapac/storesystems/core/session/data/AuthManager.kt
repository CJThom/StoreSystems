package com.gpcasiapac.storesystems.core.session.data

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.AuthSessionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthManager(
    private val sessionDao: AuthSessionDao,
    private val identityRepository: IdentityRepository,
    private val logger: Logger,
) {
    private val log = logger.withTag("AuthManager")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        scope.launch {
            sessionDao.observeSession().collectLatest { s ->
                if (s == null) return@collectLatest
                val now = System.currentTimeMillis()
                val expiresAt = s.issuedAt + s.expiresIn * 1000
                val refreshDelayMs = ((expiresAt - now) * 0.8).toLong().coerceAtLeast(5_000)
                delay(refreshDelayMs)
                val result = identityRepository.refreshToken(s.refreshToken)
                if (result is DataResult.Error) {
                    log.e { "Token refresh failed: ${result.message}" }
                }
            }
        }
    }
}
