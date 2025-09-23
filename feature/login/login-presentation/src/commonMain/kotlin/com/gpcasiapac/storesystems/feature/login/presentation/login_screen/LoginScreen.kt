package com.gpcasiapac.storesystems.feature.login.presentation.login_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    state: LoginScreenContract.State,
    onEventSent: (event: LoginScreenContract.Event) -> Unit,
    effectFlow: Flow<LoginScreenContract.Effect>,
    onExternalOutcome: (effect: LoginScreenContract.Effect.Outcome) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is LoginScreenContract.Effect.ShowToast -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is LoginScreenContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.error,
                        duration = SnackbarDuration.Long
                    )
                }

                is LoginScreenContract.Effect.Outcome -> onExternalOutcome(effect)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = state.username,
                onValueChange = { onEventSent(LoginScreenContract.Event.UpdateUsername(it)) },
                label = { Text("Username") },
                enabled = !state.isLoading,
                isError = state.error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = { onEventSent(LoginScreenContract.Event.UpdatePassword(it)) },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                enabled = !state.isLoading,
                isError = state.error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            if (state.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Button(
                onClick = { onEventSent(LoginScreenContract.Event.SubmitCredentials) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isLoginEnabled && !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (state.isLoading) "Logging in..." else "Login",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter any username and password to continue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
