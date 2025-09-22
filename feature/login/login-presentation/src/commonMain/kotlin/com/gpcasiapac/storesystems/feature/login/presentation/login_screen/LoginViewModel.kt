package com.gpcasiapac.storesystems.feature.login.presentation.login_screen

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.feature.login.api.LoginFlags

class LoginViewModel(
    private val loginService: LoginService,
    private val flags: FeatureFlags
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
        // Initialize any startup logic here if needed
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: LoginScreenContract.Event) {
        when (event) {
            is LoginScreenContract.Event.UpdateUsername -> updateUsername(event.username)
            is LoginScreenContract.Event.UpdatePassword -> updatePassword(event.password)
            is LoginScreenContract.Event.Login -> {
                viewModelScope.launch {
                    performLogin(
                        onSuccess = {
                            setEffect { LoginScreenContract.Effect.ShowToast("Login successful!") }
                            setEffect { LoginScreenContract.Effect.Navigation.NavigateToHome }
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
            is LoginUseCase.UseCaseResult.Success -> {
                setState {
                    copy(
                        isLoading = false,
                        error = null
                    )
                }
                // Feature flag: if MFA is required, don't navigate to home yet
                if (flags.isEnabled(LoginFlags.MfaRequired)) {
                    setEffect { LoginScreenContract.Effect.ShowToast("MFA required (demo)") }
                    val uid = viewState.value.username.ifBlank { "user" }
                    setEffect { LoginScreenContract.Effect.Navigation.NavigateToOtp(uid) }
                } else {
                    onSuccess()
                }
            }

            is LoginUseCase.UseCaseResult.Error -> {
                setState {
                    copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                onError(result.message)
            }
        }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
