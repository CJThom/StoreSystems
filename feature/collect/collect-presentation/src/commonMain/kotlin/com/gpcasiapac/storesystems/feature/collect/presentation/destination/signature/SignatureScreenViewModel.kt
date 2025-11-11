package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectOrderWithCustomerWithLineItemsListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SaveSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper.toSignatureSummary
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState
import com.gpcasiapac.storesystems.feature.collect.presentation.util.imageBitmapToBase64Encoded
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SignatureScreenViewModel(
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase,
    private val observeCollectOrderWithCustomerWithLineItemsListUseCase: ObserveCollectOrderWithCustomerWithLineItemsListUseCase,
    private val saveSignatureUseCase: SaveSignatureUseCase
) : MVIViewModel<
        SignatureScreenContract.Event,
        SignatureScreenContract.State,
        SignatureScreenContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ) {

    override fun setInitialState(): SignatureScreenContract.State =
        SignatureScreenContract.State(
            isLoading = false,
            isSigned = false,
            error = null,
            signatureStrokes = emptyList(),
            signatureBitmap = null, // TODO: use this instead of the strokes?
            customerName = "",
            summary = SignatureSummaryState.Multi(
                invoiceNumberList = emptyList(),
                totalQuantity = 0
            ),
        )

    override suspend fun awaitReadiness(): Boolean {
        val collectSessionIds = sessionState.first { it.userId != null }
        return collectSessionIds.userId != null
    }

    override fun handleReadinessFailed() {
        // Not applicable in this placeholder
    }

    override fun onStart() {

        viewModelScope.launch {
            observeCollectOrders()
        }

    }

    private suspend fun observeCollectOrders() {
        sessionState
            .map { it.workOrderId }
            .distinctUntilChanged()
            .flatMapLatest { workOrderId: WorkOrderId? ->
                if (workOrderId == null) {
                    // Emit a null marker to be handled downstream
                    flowOf(null)
                } else {
                    // Map the non-null list through as-is
                    observeCollectOrderWithCustomerWithLineItemsListUseCase(workOrderId)
                }
            }.collectLatest { collectOrderListOrNull ->
                if (collectOrderListOrNull == null) {
                    // Null work order id: show error and reset summary
                    setState {
                        copy(
                            isLoading = false,
                            error = "No Work Order Selected",
                            summary = SignatureSummaryState.Multi(
                                invoiceNumberList = emptyList(),
                                totalQuantity = 0
                            )
                        )
                    }
                    return@collectLatest
                }

                if (collectOrderListOrNull.isEmpty()) {
                    setState {
                        copy(
                            isLoading = false,
                            summary = SignatureSummaryState.Multi(
                                invoiceNumberList = emptyList(),
                                totalQuantity = 0
                            )
                        )
                    }
                    return@collectLatest
                }

                setState {
                    copy(
                        summary = collectOrderListOrNull.toSignatureSummary(),
                        isLoading = false
                    )
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
                            val workOrderId: WorkOrderId =
                                sessionState.value.workOrderId.handleNull() ?: return@launch
                            // Convert ImageBitmap to byte array using KMP-compatible approach
                            val base64String =
                                imageBitmapToBase64Encoded(imageBitmap = signatureBitmap)
                            // Call the use case with correct parameters
                            saveSignatureUseCase(
                                workOrderId = workOrderId,
                                signatureBase64 = base64String,
                                signedByName = viewState.value.customerName
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

            is SignatureScreenContract.Event.ViewDetailsClicked -> {
                setEffect { SignatureScreenContract.Effect.Outcome.OpenWorkOrderDetails }
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

    private fun WorkOrderId?.handleNull(): WorkOrderId? {
        if (this == null) {
            setState { copy(error = "No Work Order Selected") }
        }
        return this
    }
}
