package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveLatestOpenWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.SaveSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.util.imageBitmapToBase64Encoded
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignatureScreenViewModel(
    private val observeLatestOpenWorkOrderUseCase: ObserveLatestOpenWorkOrderUseCase,
    private val saveSignatureUseCase: SaveSignatureUseCase
) : MVIViewModel<
        SignatureScreenContract.Event,
        SignatureScreenContract.State,
        SignatureScreenContract.Effect>() {

    override fun setInitialState(): SignatureScreenContract.State =
        SignatureScreenContract.State(
            isLoading = false,
            isSigned = false,
            error = null,
            signatureStrokes = emptyList(),
            customerName = ""
        )

    override suspend fun awaitReadiness(): Boolean {
        // No special readiness needed for placeholder signature capture
        return true
    }

    override fun handleReadinessFailed() {
        // Not applicable in this placeholder
    }

    override fun onStart() {
        viewModelScope.launch {
            observeLatestOpenWorkOrderUseCase("mock").collectLatest {
                // TODO: Add signature strokes
                setState { copy() }
            }
        }
    }

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: SignatureScreenContract.Event) {
        when (event) {
            is SignatureScreenContract.Event.StartCapture -> {
                val signatureBitmap = viewState.value.signatureBitmap
                if (signatureBitmap != null) {
                    viewModelScope.launch {
                        try {
                            // Convert ImageBitmap to byte array using KMP-compatible approach
                            val base64String =
                                imageBitmapToBase64Encoded(imageBitmap = signatureBitmap)
                            // Call the use case with correct parameters
                            saveSignatureUseCase(
                                userRefId = "mock",
                                base64Signature = base64String,
                                signedByName = viewState.value.customerName.trim().ifBlank { null }
                            )

                            // Set success effect
                            setEffect {
                                SignatureScreenContract.Effect.Outcome.SignatureSaved(
                                    viewState.value.signatureStrokes
                                )
                            }
                        } catch (e: Exception) {
                            setEffect {
                                SignatureScreenContract.Effect.ShowError("Failed to save signature: ${e.message}")
                            }
                        }
                    }
                } else {
                    setEffect {
                        SignatureScreenContract.Effect.ShowToast("Please draw a signature first")
                    }
                }
            }

            is SignatureScreenContract.Event.StrokesChanged -> {
                setState {
                    copy(
                        signatureStrokes = event.strokes,
                        isSigned = event.strokes.isNotEmpty()
                    )
                }

            }

            is SignatureScreenContract.Event.ClearError -> clearError()
            is SignatureScreenContract.Event.SetCustomerName -> {
                setState { copy(customerName = event.name) }
            }
            is SignatureScreenContract.Event.Back -> setEffect { Back }
            is SignatureScreenContract.Event.SignatureCompleted -> {
                setState { copy(signatureBitmap = event.signatureBitmap) }
            }

            SignatureScreenContract.Event.ClearSignature -> {
                setState {
                    copy(
                        signatureStrokes = emptyList(),
                        isSigned = false,
                        signatureBitmap = null
                    )
                }
            }
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
