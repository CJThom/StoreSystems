package com.gpcasiapac.storesystems.feature.login.presentation.login_screen

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginService: LoginService
    // ❌ REMOVED: private val flags: FeatureFlags
) : MVIViewModel<LoginScreenContract.Event, LoginScreenContract.State, LoginScreenContract.Effect>() {

    override fun setInitialState(): LoginScreenContract.State {
        return LoginScreenContract.State(
            username = "",
            password = "",
            isLoading = false,
            isLoginEnabled = false,
            error = null
        )
    }

    override suspend fun awaitReadiness(): Boolean {
        // Login screen doesn't require session readiness
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable for login screen
    }

    override fun onStart() {
        // ❌ REMOVED: Feature flag initialization
        // Feature flags are initialized at app startup, not in ViewModel
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: LoginScreenContract.Event) {
        when (event) {
            is LoginScreenContract.Event.UpdateUsername -> updateUsername(event.username)
            is LoginScreenContract.Event.UpdatePassword -> updatePassword(event.password)
            is LoginScreenContract.Event.SubmitCredentials -> {
                viewModelScope.launch {
                    performLogin(
                        onSuccess = {
                            setEffect { LoginScreenContract.Effect.ShowToast("Login successful!") }
                            setEffect { LoginScreenContract.Effect.Outcome.LoginCompleted }
                        },
                        onError = { errorMessage ->
                            setEffect { LoginScreenContract.Effect.ShowError(errorMessage) }
                        }
                    )
                }
            }

            is LoginScreenContract.Event.ClearError -> clearError()
        }
    }

    private fun updateUsername(username: String) {
        setState {
            copy(
                username = username,
                isLoginEnabled = username.isNotBlank() && password.isNotBlank(),
                error = null
            )
        }
    }

    private fun updatePassword(password: String) {
        setState {
            copy(
                password = password,
                isLoginEnabled = username.isNotBlank() && password.isNotBlank(),
                error = null
            )
        }
    }

    private suspend fun performLogin(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }

        when (val result = loginService.login(
            viewState.value.username,
            viewState.value.password
        )) {
            is DataResult.Success -> {
                setState {
                    copy(
                        isLoading = false,
                        error = null
                    )
                }
                
                // ❌ REMOVED: Feature flag context update - handled in domain layer
                // ❌ REMOVED: Feature flag check - handled in domain layer
                
                // Get MFA requirement from result metadata
                val mfaRequired = result.data.metadata["mfaRequired"] as? Boolean ?: false
                val mfaVersion = result.data.metadata["mfaVersion"] as? String ?: "v1"
                
                // Use case tells us if MFA is required
                if (mfaRequired) {
                    setEffect { LoginScreenContract.Effect.ShowToast("Login successful!") }
                    setEffect { 
                        LoginScreenContract.Effect.ShowToast("MFA required ($mfaVersion)") 
                    }
                    val uid = viewState.value.username.ifBlank { "user" }
                    setEffect { LoginScreenContract.Effect.Outcome.MfaRequired(uid) }
                } else {
                    onSuccess()
                }
            }

            is DataResult.Error -> {
                val message = when (result) {
                    is DataResult.Error.Network.ConnectionError ->
                        "Network connection error. Please check your internet connection."
                    is DataResult.Error.Network.HttpError -> when (result.code) {
                        400, 401, 404 -> "Invalid username or password. Please try again."
                        403 -> "Account is locked. Please contact support."
                        429 -> "Too many login attempts. Please try again later."
                        else -> "Login service is currently unavailable. Please try again later."
                    }
                    is DataResult.Error.Network.SerializationError ->
                        "Login failed due to serialization error. Please try again."
                    is DataResult.Error.Network.UnknownError ->
                        "An unknown network error occurred. Please try again."
                    is DataResult.Error.Client.Database ->
                        "Login service is currently unavailable. Please try again later."
                    is DataResult.Error.Client.Mapping ->
                        "Login failed. Please try again."
                    is DataResult.Error.Client.UnexpectedError ->
                        result.message
                }
                setState { copy(isLoading = false, error = message) }
                onError(message)
            }
        }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
