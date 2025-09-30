package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignatureScreenViewModel : MVIViewModel<
        SignatureScreenContract.Event,
        SignatureScreenContract.State,
        SignatureScreenContract.Effect>() {

    override fun setInitialState(): SignatureScreenContract.State =
        SignatureScreenContract.State(
            isLoading = false,
            isSigned = false,
            error = null,
            signatureStrokes = emptyList(),
        )

    override suspend fun awaitReadiness(): Boolean {
        // No special readiness needed for placeholder signature capture
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable in this placeholder
    }

    override fun onStart() {
        // No-op: nothing to load initially for signature
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: SignatureScreenContract.Event) {
        when (event) {
            is SignatureScreenContract.Event.StartCapture -> {
                val strokes = viewState.value.signatureStrokes
                if (strokes.isNotEmpty()) {
                    setEffect { SignatureScreenContract.Effect.Outcome.SignatureSaved(strokes) }
                }
            }

            is SignatureScreenContract.Event.StrokesChanged -> {
                setState { copy(signatureStrokes = event.strokes, isSigned = event.strokes.isNotEmpty()) }
            }

            is SignatureScreenContract.Event.ClearError -> clearError()
            is SignatureScreenContract.Event.Back -> setEffect { SignatureScreenContract.Effect.Outcome.Back }
        }
    }

    private suspend fun performSignatureCapture(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        setState { copy(isLoading = true, error = null) }
        try {
            // Simulate signature capture delay
            delay(400)
            setState { copy(isLoading = false, isSigned = true, error = null) }
            onSuccess()
        } catch (t: Throwable) {
            val message = t.message ?: "Failed to capture signature. Please try again."
            setState { copy(isLoading = false, error = message) }
            onError(message)
        }
    }

    private fun clearError() {
        setState { copy(error = null) }
    }
}
