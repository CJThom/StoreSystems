package com.gpcasiapac.storesystems.feature.login.api

sealed interface LoginOutcome {
    data class MfaRequired(val userId: String) : LoginOutcome
    data object LoginCompleted : LoginOutcome
    data object Back : LoginOutcome
}