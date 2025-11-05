package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.material.icons.Icons
import com.gpcasiapac.storesystems.common.kotlin.extension.Components
import com.gpcasiapac.storesystems.common.kotlin.extension.DateStyle
import com.gpcasiapac.storesystems.common.kotlin.extension.HourCycle
import com.gpcasiapac.storesystems.common.kotlin.extension.TimeStyle
import com.gpcasiapac.storesystems.common.kotlin.extension.formatLocal
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ValidateScannedInvoiceInputUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddScannedInputToWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderItemsInScanOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCollectingTypeUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCourierNameUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SubmitOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.util.Debouncer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull


class OrderFulfilmentScreenViewModel(
    logger: Logger,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val observeCollectWorkOrderUseCase: ObserveCollectWorkOrderUseCase,
    private val observeWorkOrderItemsInScanOrderUseCase: ObserveWorkOrderItemsInScanOrderUseCase,
    private val setWorkOrderCollectingTypeUseCase: SetWorkOrderCollectingTypeUseCase,
    private val setWorkOrderCourierNameUseCase: SetWorkOrderCourierNameUseCase,
    private val addScannedInputToWorkOrderUseCase: AddScannedInputToWorkOrderUseCase,
    private val validateScannedInvoiceInputUseCase: ValidateScannedInvoiceInputUseCase,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val observeWorkOrderSignatureUseCase: ObserveWorkOrderSignatureUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase
) : MVIViewModel<
        OrderFulfilmentScreenContract.Event,
        OrderFulfilmentScreenContract.State,
        OrderFulfilmentScreenContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ) {

    private val log = logger.withTag("OrderFulfilmentScreenViewModel")

    // Shared keyed debouncer for persisting user edits
    private val debouncer = Debouncer(viewModelScope)

    // Shared WorkOrderId flow to drive all downstream observations (Option B)
    private val workOrderIdFlow by lazy {
        sessionState
            .map { it.workOrderId }
            .onEach { id -> if (id == null) setState { copy(error = "No Work Order Selected") } }
            .filterNotNull()
            .distinctUntilChanged()
            .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)
    }

    override fun setInitialState(): OrderFulfilmentScreenContract.State {
        return OrderFulfilmentScreenContract.State(
            collectOrderListItemStateList = listOf(CollectOrderListItemState.placeholder()),
            isLoading = false,
            isProcessing = false,
            error = null,
            featureFlags = OrderFulfilmentScreenContract.State.FeatureFlags(
                isAccountCollectingFeatureEnabled = false,
                isCorrespondenceSectionVisible = false,
            ),
            collectingType = null,
            collectionTypeOptionList = listOf(
                CollectionTypeSectionDisplayState(
                    enabled = true,
                    collectingType = CollectingType.STANDARD,
                    icon = Icons.Outlined.Person,
                    label = CollectingType.STANDARD.name,
                ), CollectionTypeSectionDisplayState(
                    enabled = true,
                    collectingType = CollectingType.ACCOUNT,
                    icon = Icons.Outlined.Business,
                    label = CollectingType.ACCOUNT.name,
                ), CollectionTypeSectionDisplayState(
                    enabled = true,
                    collectingType = CollectingType.COURIER,
                    icon = Icons.Outlined.LocalShipping,
                    label = CollectingType.COURIER.name,
                )
            ),
            representativeSearchQuery = "",
            representativeList = listOf(
                Representative("rep-1", "John Doe", "#9288180049912"),
                Representative("rep-2", "Custa Ma", "#9288180049912"),
                Representative("rep-3", "Alice Smith", "#9288180049912"),
            ),
            selectedRepresentativeIds = emptySet(),
            courierName = "",
            signatureStrokes = emptyList(),
            signatureBase64 = null,
            isCustomerNameDialogVisible = false,
            customerNameInput = "",
            correspondenceOptionList = listOf(
                CorrespondenceItemDisplayParam(
                    id = "email",
                    type = "Email",
                    detail = "Send email to customer",
                    isEnabled = true
                ),
                CorrespondenceItemDisplayParam(
                    id = "print",
                    type = "Print",
                    detail = "Send invoice to printer",
                    isEnabled = true
                )
            ),
            isSighted = false
        )

    }

    override suspend fun awaitReadiness(): Boolean {
        val collectSessionIds = sessionState.first { it.userId != null }
        return collectSessionIds.userId != null
    }

    override fun handleReadinessFailed() {
        // TODO:  handleReadinessFailed
    }

    override fun onStart() {
        // Option B: three independent pipelines sharing workOrderIdFlow
        // Header (Collect Work Order)
        viewModelScope.launch {
            workOrderIdFlow
                .flatMapLatest { id -> observeCollectWorkOrderUseCase(id) }
                .collectLatest { wo ->
                    setState {
                        copy(
                            collectingType = wo?.collectingType,
                            courierName = wo?.courierName ?: "",
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }

        // Items
        viewModelScope.launch {
            workOrderIdFlow
                .flatMapLatest { id -> observeWorkOrderItemsInScanOrderUseCase(id) }
                .collectLatest { items ->
                    setState { copy(collectOrderListItemStateList = items.toListItemState()) }
                }
        }

        // Signature
        viewModelScope.launch {
            workOrderIdFlow
                .flatMapLatest { id -> observeWorkOrderSignatureUseCase(id) }
                .collectLatest { signature ->
                    setState {
                        copy(
                            signatureBase64 = signature?.signatureBase64,
                            signerName = signature?.signedByName,
                            signedDateTime = signature?.signedAt
                        )
                    }
                }
        }
    }

//    signedDateTime = signature?.signedAt?.formatLocal(
//    components = Components.DateTime,
//    dateStyle = DateStyle.Medium,
//    timeStyle = TimeStyle.Short,
//    hourCycle = HourCycle.H12,
//    )

    // TABLE OF CONTENTS - All possible events handled here
    override fun handleEvents(event: OrderFulfilmentScreenContract.Event) {
        when (event) {

            is OrderFulfilmentScreenContract.Event.Refresh -> {
                viewModelScope.launch {
                    fetchOrders(successToast = "Orders refreshed")
                }
            }

            is OrderFulfilmentScreenContract.Event.ClearError -> {
                setState { copy(error = null) }
            }

            is OrderFulfilmentScreenContract.Event.Back -> {
                setEffect { OrderFulfilmentScreenContract.Effect.Outcome.Back }
            }

            // Collecting selector
            is OrderFulfilmentScreenContract.Event.CollectingChanged -> {
                onCollectingChanged(event.type)
            }

            // Account flow
            is OrderFulfilmentScreenContract.Event.RepresentativeSearchQueryChanged -> {
                setState { copy(representativeSearchQuery = event.query) }
            }

            is OrderFulfilmentScreenContract.Event.RepresentativeSelected -> {
                onRepresentativeChecked(event.id, event.isSelected)
            }

            is OrderFulfilmentScreenContract.Event.ClearRepresentativeSelection -> {
                setState { copy(selectedRepresentativeIds = emptySet()) }
            }

            // Courier flow
            is OrderFulfilmentScreenContract.Event.CourierNameChanged -> {
                // Optimistic UI update
                setState { copy(courierName = event.text) }

                // Debounced persistence using screen preset
                debouncer.submit(OrderFulfilmentScreenContract.Debounce.CourierName) {
                    val s = viewState.value
                    if (s.collectingType == CollectingType.COURIER) {
                        val workOrderId: WorkOrderId =
                            sessionState.value.workOrderId.handleNull() ?: return@submit
                        setWorkOrderCourierNameUseCase(
                            workOrderId = workOrderId,
                            name = s.courierName.trim()
                        )

                    }
                }
            }

            is OrderFulfilmentScreenContract.Event.ClearCourierName -> {
                setState { copy(courierName = "") }
            }

            // Signature
            is OrderFulfilmentScreenContract.Event.Sign -> {
                sign()
            }

            // ID sighted
            is OrderFulfilmentScreenContract.Event.IdSightedChanged -> {
                setState { copy(isSighted = event.checked) }
            }

            // ID Verification (single-select)
            is OrderFulfilmentScreenContract.Event.IdVerificationChanged -> {
                setState {
                    copy(
                        idVerification = event.option,
                        idVerificationOtherText = if (event.option == OrderFulfilmentScreenContract.IdVerificationOption.OTHER) idVerificationOtherText else ""
                    )
                }
            }

            is OrderFulfilmentScreenContract.Event.IdVerificationOtherChanged -> {
                setState { copy(idVerificationOtherText = event.text) }
            }

            is OrderFulfilmentScreenContract.Event.ShowCustomerNameDialog -> {
                setState { copy(isCustomerNameDialogVisible = true) }
            }

            is OrderFulfilmentScreenContract.Event.DismissCustomerNameDialog -> {
                setState { copy(isCustomerNameDialogVisible = false) }
            }

            is OrderFulfilmentScreenContract.Event.CustomerNameChanged -> {
                setState { copy(customerNameInput = event.text) }
            }

            is OrderFulfilmentScreenContract.Event.ConfirmCustomerName -> {
                val name = viewState.value.customerNameInput.trim()
                if (name.isEmpty()) {
                    setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Please enter customer name") }
                } else {
                    setState { copy(isCustomerNameDialogVisible = false) }
                    setEffect { OrderFulfilmentScreenContract.Effect.Outcome.SignatureRequested(name) }
                }
            }

            is OrderFulfilmentScreenContract.Event.SignatureSaved -> {
                onSignatureSaved(event.strokes)
            }

            is OrderFulfilmentScreenContract.Event.ClearSignature -> {
                clearSignature()
            }


            // Correspondence
            is OrderFulfilmentScreenContract.Event.ToggleCorrespondence -> {
                onCorrespondenceToggled(event.id)
            }

            is OrderFulfilmentScreenContract.Event.EditCorrespondence -> {
                setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Edit ${event.id} not implemented") }
            }

            // Final action
            is OrderFulfilmentScreenContract.Event.Confirm -> {
                confirm()
            }

            // Search-origin selection confirmation
            is OrderFulfilmentScreenContract.Event.ConfirmSearchSelection -> {
                setEffect { OrderFulfilmentScreenContract.Effect.ShowConfirmSelectionDialog() }
            }

            is OrderFulfilmentScreenContract.Event.ConfirmSearchSelectionProceed -> {
                // No-op; SearchViewModel will handle persistence and collapse
            }

            is OrderFulfilmentScreenContract.Event.DismissConfirmSearchSelectionDialog -> {
                // No-op; UI dismisses dialog
            }

            is OrderFulfilmentScreenContract.Event.OrderClicked -> {
                setEffect {
                    OrderFulfilmentScreenContract.Effect.Outcome.NavigateToOrderDetails(
                        event.invoiceNumber
                    )
                }
            }

            is OrderFulfilmentScreenContract.Event.ScanInvoice -> {
                val invoice = event.rawInput
                // Collapse search on scan (handled by UI via effect)
                setEffect { OrderFulfilmentScreenContract.Effect.CollapseSearchBar }
                viewModelScope.launch {
                    when (val result = validateScannedInvoiceInputUseCase(rawInput = invoice)) {
                        is ValidateScannedInvoiceInputUseCase.UseCaseResult.Exists -> {
                            if (event.autoSelect) {
                                val session = sessionState.value
                                when (val res = addScannedInputToWorkOrderUseCase(
                                    userId = session.userId,
                                    currentSelectedWorkOrderId = session.workOrderId,
                                    rawInput = invoice
                                )) {
                                    is AddScannedInputToWorkOrderUseCase.UseCaseResult.Added -> {
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlayHaptic(
                                                HapticEffect.Success
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlaySound(
                                                SoundEffect.Success
                                            )
                                        }
                                    }

                                    is AddScannedInputToWorkOrderUseCase.UseCaseResult.Duplicate -> {
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlayHaptic(
                                                HapticEffect.SelectionChanged
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlaySound(
                                                SoundEffect.Warning
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                                                "Order already added: \"${res.invoiceNumber}\""
                                            )
                                        }
                                    }

                                    is AddScannedInputToWorkOrderUseCase.UseCaseResult.NotFound -> {
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlayHaptic(
                                                HapticEffect.Error
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlaySound(
                                                SoundEffect.Error
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                                                "Order not found: \"${res.input}\""
                                            )
                                        }
                                    }

                                    is AddScannedInputToWorkOrderUseCase.UseCaseResult.InvalidInput -> {
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlayHaptic(
                                                HapticEffect.Error
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlaySound(
                                                SoundEffect.Error
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                                                "Invalid scan input"
                                            )
                                        }
                                    }

                                    is AddScannedInputToWorkOrderUseCase.UseCaseResult.Error -> {
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlayHaptic(
                                                HapticEffect.Error
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.PlaySound(
                                                SoundEffect.Error
                                            )
                                        }
                                        setEffect {
                                            OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                                                res.message
                                            )
                                        }
                                    }
                                }
                            } else {
                                setEffect {
                                    OrderFulfilmentScreenContract.Effect.Outcome.NavigateToOrderDetails(
                                        result.invoiceNumber
                                    )
                                }
                            }
                        }

                        is ValidateScannedInvoiceInputUseCase.UseCaseResult.Error -> {
                            setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                            setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Error) }
                            setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                    }
                }
            }

            is OrderFulfilmentScreenContract.Event.DeselectOrder -> {
                val workOrderId: WorkOrderId = sessionState.value.workOrderId.handleNull() ?: return
                viewModelScope.launch {
                    removeOrderSelectionUseCase(
                        workOrderId = workOrderId,
                        invoiceNumber = event.invoiceNumber
                    )
                }
            }
        }
    }

    private fun onCorrespondenceToggled(id: String) {
        setState {
            val updatedOptions = correspondenceOptionList.map {
                if (it.id == id) {
                    it.copy(isEnabled = !it.isEnabled)
                } else {
                    it
                }
            }
            copy(correspondenceOptionList = updatedOptions)
        }
    }


    private suspend fun fetchOrders(successToast: String) {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }
        when (val result = fetchOrderListUseCase()) {
            is FetchOrderListUseCase.UseCaseResult.Success -> {
                setState { copy(isLoading = false) }
            }

            is FetchOrderListUseCase.UseCaseResult.Error -> {
                val msg = result.message
                setState { copy(isLoading = false, error = msg) }
                setEffect {
                    OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                        msg,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    private fun onCollectingChanged(type: CollectingType) {
        val current = viewState.value
        if (current.collectingType == type) return

        // Optimistic UI update
        setState {
            copy(
                collectingType = type,
                representativeSearchQuery = if (type == CollectingType.ACCOUNT) representativeSearchQuery else "",
                selectedRepresentativeIds = if (type == CollectingType.ACCOUNT) selectedRepresentativeIds else emptySet(),
                courierName = if (type == CollectingType.COURIER) courierName else "",
            )
        }

        // Debounced DB update to keep Work Order as source of truth
        debouncer.submit(
            OrderFulfilmentScreenContract.Debounce.CollectingType
        ) {
            val workOrderId: WorkOrderId =
                sessionState.value.workOrderId.handleNull() ?: return@submit
            setWorkOrderCollectingTypeUseCase(workOrderId = workOrderId, type)
        }
    }

    private fun onRepresentativeChecked(representativeId: String, isSelected: Boolean) {
        setState {
            val newSet = selectedRepresentativeIds.toMutableSet()
            if (isSelected) newSet.add(representativeId) else newSet.remove(representativeId)
            copy(selectedRepresentativeIds = newSet)
        }
    }

    private fun sign() {
        // Open customer name dialog before navigating to signature screen
        setState { copy(isCustomerNameDialogVisible = true) }
    }

    private fun onSignatureSaved(strokes: List<List<Offset>>) {
        setState { copy(signatureStrokes = strokes) }
        setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Signature saved") }
    }

    private fun clearSignature() {
        if (viewState.value.signatureStrokes.isNotEmpty()) {
            setState { copy(signatureStrokes = emptyList()) }
            setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Signature cleared") }
        }
    }

    private fun confirm() {
        val s = viewState.value
        val workOrderId = sessionState.value.workOrderId.handleNull() ?: return
        val hasOrders = s.collectOrderListItemStateList.isNotEmpty()

        if (!hasOrders) {
            setEffect {
                OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                    "No orders to confirm",
                    duration = SnackbarDuration.Long
                )
            }
            return
        }
        when (s.collectingType) {
            CollectingType.ACCOUNT -> {
                if (s.selectedRepresentativeIds.isEmpty()) {
                    setEffect {
                        OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                            "Please select at least one representative",
                            duration = SnackbarDuration.Long
                        )
                    }
                    return
                }
            }

            CollectingType.COURIER -> {
                if (s.courierName.isBlank()) {
                    setEffect {
                        OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                            "Please enter the courier name",
                            duration = SnackbarDuration.Long
                        )
                    }
                    return
                }
            }

            CollectingType.STANDARD -> Unit
            else -> Unit
        }

        // Start processing - add orders to sync queue
        viewModelScope.launch {
            setState { copy(isProcessing = true) }
            setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("Processing orders...") }

            // Add delay for UX feedback
            delay(1500)

            // Submit the entire work order as one batched task
            val workOrderId = workOrderIdFlow.firstOrNull()
            if (workOrderId == null) {
                setState { copy(isProcessing = false) }
                setEffect { OrderFulfilmentScreenContract.Effect.ShowSnackbar("No active work order", duration = SnackbarDuration.Long) }
                return@launch
            }

            val result = submitOrderUseCase(workOrderId)

            setState { copy(isProcessing = false) }

            result.fold(
                onSuccess = {
                    setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.Success) }
                    setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Success) }
                    setEffect {
                        OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                            "Successfully queued ${viewState.value.collectOrderListItemStateList.size} order(s) for sync",
                            duration = SnackbarDuration.Long
                        )
                    }
                    setEffect { OrderFulfilmentScreenContract.Effect.Outcome.Confirmed }
                },
                onFailure = { e ->
                    setEffect { OrderFulfilmentScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                    setEffect { OrderFulfilmentScreenContract.Effect.PlaySound(SoundEffect.Error) }
                    setEffect {
                        OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                            e.message ?: "Failed to queue orders",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            )
        }
    }

    private fun WorkOrderId?.handleNull(): WorkOrderId? {
        log.d { "WorkOrderId is $this" }
        if (this == null) {
            setState { copy(error = "No Work Order Selected") }
        }
        return this
    }


}
