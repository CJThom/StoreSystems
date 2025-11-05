package com.gpcasiapac.storesystems.feature.login.api

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId

sealed interface LoginOutcome {
    data class MfaRequired(val userId: UserId) : LoginOutcome
    data object LoginCompleted : LoginOutcome
    data object Back : LoginOutcome
}