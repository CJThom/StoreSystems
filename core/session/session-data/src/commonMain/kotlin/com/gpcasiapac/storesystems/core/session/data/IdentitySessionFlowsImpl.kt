package com.gpcasiapac.storesystems.core.session.data

import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.AuthSessionDao
import com.gpcasiapac.storesystems.core.session.api.IdentitySessionFlows
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class IdentitySessionFlowsImpl(
    private val sessionDao: AuthSessionDao,
    logger: Logger,
) : IdentitySessionFlows {

    private val log = logger.withTag("IdentitySessionFlows")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile
    private var tokenCache: String? = null

    init {
        // Keep a hot cache of the latest token for interceptors
        scope.launch {
            sessionDao.observeSession().collect { s ->
                tokenCache = s?.accessToken
            }
        }
    }

    override fun userIdFlow(): Flow<String?> = sessionDao.observeSession().map { it?.userId }

    override fun tokenSnapshot(): () -> String? = { tokenCache }
}
