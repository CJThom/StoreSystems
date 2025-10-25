package com.gpcasiapac.storesystems.core.session.api

import kotlinx.coroutines.flow.Flow

// Marker for typed session id compositions used by ViewModels via SessionHandlerDelegate
interface SessionIds

// Read-only adapter exposing identity-derived session flows
interface IdentitySessionFlows {
    fun userIdFlow(): Flow<String?>
    fun tokenSnapshot(): () -> String?
}
