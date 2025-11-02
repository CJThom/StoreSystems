package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveCollectOrderWithCustomerWithLineItemsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderWithOrderWithCustomersUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SaveSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenContract.Effect.Outcome.Back
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper.toSignatureOrderStateList
import com.gpcasiapac.storesystems.feature.collect.presentation.util.imageBitmapToBase64Encoded
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SignatureScreenViewModel(
    private val observeCollectWorkOrderUseCase: ObserveCollectWorkOrderUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase,
    private val observeWorkOrderWithOrderWithCustomersUseCase: ObserveWorkOrderWithOrderWithCustomersUseCase,
    private val observeCollectOrderWithCustomerWithLineItemsUseCase: ObserveCollectOrderWithCustomerWithLineItemsUseCase,
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
            val workOrderId: WorkOrderId =
                sessionState.value.workOrderId.handleNull() ?: return@launch
            observeWorkOrderWithOrderWithCustomersUseCase(workOrderId = workOrderId).collectLatest { workOrderWithOrders ->
                val invoiceNumbers = workOrderWithOrders
                    ?.collectOrderWithCustomerList
                    ?.map { it.order.invoiceNumber }
                    ?: emptyList()

                if (invoiceNumbers.isEmpty()) {
                    setState { copy(selectedOrderList = emptyList(), isLoading = false) }
                    return@collectLatest
                }

                setState { copy(isLoading = true, error = null) }

                val flows = invoiceNumbers.map { invoice ->
                    observeCollectOrderWithCustomerWithLineItemsUseCase(invoice)
                }

                val combinedFlow = if (flows.size == 1) {
                    flows.first().map { listOf(it) }
                } else {
                    combine(flows) { it.toList() }
                }

                combinedFlow.collectLatest { domainList ->
                    val nonNullDomain = domainList.filterNotNull()
                    val presentationList = nonNullDomain.map { it.toState() }
                    val signatureOrders = presentationList.toSignatureOrderStateList()
                    setState { copy(selectedOrderList = signatureOrders, isLoading = false) }
                }
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
                val invoices = viewState.value.selectedOrderList.map { it.invoiceNumber }
                setEffect { SignatureScreenContract.Effect.Outcome.OpenWorkOrderDetails(invoices) }
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
