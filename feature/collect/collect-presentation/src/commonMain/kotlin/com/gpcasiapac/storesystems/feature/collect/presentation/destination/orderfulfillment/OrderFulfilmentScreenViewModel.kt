package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.compose.DialogButton
import com.gpcasiapac.storesystems.common.presentation.compose.StringWrapper.Text
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectionTypeGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ValidateScannedInvoiceInputUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddScannedInputToWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveCollectionTypeGatingUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderItemsInScanOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveWorkOrderSignatureUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.RemoveOrderSelectionUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCollectingTypeUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SetWorkOrderCourierNameUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.SubmitOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenContract.Effect.*
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenContract.Effect.Outcome.*
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract.Event.Selection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderListItemState
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
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
import kotlinx.coroutines.flow.combine


class OrderFulfilmentScreenViewModel(
    logger: Logger,
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val removeOrderSelectionUseCase: RemoveOrderSelectionUseCase,
    private val observeCollectWorkOrderUseCase: ObserveCollectWorkOrderUseCase,
    private val observeWorkOrderItemsInScanOrderUseCase: ObserveWorkOrderItemsInScanOrderUseCase,
    private val observeCollectionTypeGatingUseCase: ObserveCollectionTypeGatingUseCase,
    private val setWorkOrderCollectingTypeUseCase: SetWorkOrderCollectingTypeUseCase,
    private val setWorkOrderCourierNameUseCase: SetWorkOrderCourierNameUseCase,
    private val addScannedInputToWorkOrderUseCase: AddScannedInputToWorkOrderUseCase,
    private val validateScannedInvoiceInputUseCase: ValidateScannedInvoiceInputUseCase,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val observeWorkOrderSignatureUseCase: ObserveWorkOrderSignatureUseCase,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase,
    private val observeFulfilmentGatingUseCase: com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.ObserveFulfilmentGatingUseCase
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
                isAccountRepresentativeSelectionFeatureEnabled = false,
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
            isSighted = false,
            dialog = null
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

        viewModelScope.launch {
            observeWorkOrder()
        }

        viewModelScope.launch {
            observeOrders()
        }

        viewModelScope.launch {
            observeSignature()
        }

        viewModelScope.launch {
            observeCollectionTypeGating()
        }


        viewModelScope.launch {
            observeCohesiveGating()
        }

    }

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

            // ID verification checkbox
            is OrderFulfilmentScreenContract.Event.IdVerificationChecked -> {
                setState { copy(idVerified = event.checked) }
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
                    setEffect { ShowSnackbar("Please enter customer name") }
                } else {
                    setState { copy(isCustomerNameDialogVisible = false) }
                    setEffect { SignatureRequested(name) }
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
                setEffect { ShowSnackbar("Edit ${event.id} not implemented") }
            }

            // Final action
            is OrderFulfilmentScreenContract.Event.Confirm -> {
                confirm()
            }

            // Search-origin selection confirmation
            is OrderFulfilmentScreenContract.Event.ConfirmSearchSelection -> {
                setEffect { ShowConfirmSelectionDialog() }
            }

            is OrderFulfilmentScreenContract.Event.ConfirmSearchSelectionProceed -> {
                // No-op; SearchViewModel will handle persistence and collapse
            }

            is OrderFulfilmentScreenContract.Event.DismissConfirmSearchSelectionDialog -> {
                // No-op; UI dismisses dialog
            }

            is OrderFulfilmentScreenContract.Event.OrderClicked -> {
                setEffect {
                    NavigateToOrderDetails(
                        event.invoiceNumber
                    )
                }
            }

            is OrderFulfilmentScreenContract.Event.ScanInvoice -> {
                // Collapse search on scan (handled by UI via effect)
                setEffect { OrderFulfilmentScreenContract.Effect.CollapseSearchBar }
                viewModelScope.launch {
                    handleScan(
                        rawInput = event.rawInput,
                        isAutoSelectEnabled = event.autoSelect
                    )
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

            is OrderFulfilmentScreenContract.Event.CollapseSearchBar -> {
                setEffect { OrderFulfilmentScreenContract.Effect.CollapseSearchBar }
            }

            is OrderFulfilmentScreenContract.Event.ExpandSearchBar -> {
                setEffect { OrderFulfilmentScreenContract.Effect.ExpandSearchBar }
            }

            is OrderFulfilmentScreenContract.Event.OnAcceptMultiSelectClicked -> {
                setState {
                    copy(
                        dialog = OrderFulfilmentScreenContract.Dialog.SearchMultiSelectConfirm(
                            onProceed = DialogButton(
                                label = Text("Select"),
                                action = {
                                    setState { copy(dialog = null) }
                                    if (event.fromSearch) {
                                        setEffect { OrderFulfilmentScreenContract.Effect.ConfirmSearchSelection }
                                    } else {
                                    }
                                    setEffect { OrderFulfilmentScreenContract.Effect.CollapseSearchBar }
                                }
                            ),
                            onCancel = DialogButton(
                                label = Text("Cancel"),
                                action = {
                                    setState { copy(dialog = null) }
                                }
                            )
                        )
                    )
                }
            }
        }
    }

    // TODO: Clean up  (too much duplicate effects)
    private suspend fun handleScan(rawInput: String, isAutoSelectEnabled: Boolean) {

        when (
            val result = validateScannedInvoiceInputUseCase(rawInput = rawInput)
        ) {
            is ValidateScannedInvoiceInputUseCase.UseCaseResult.Exists -> {
                if (isAutoSelectEnabled) {
                    val session = sessionState.value
                    when (
                        val result = addScannedInputToWorkOrderUseCase(
                            userId = session.userId,
                            currentSelectedWorkOrderId = session.workOrderId,
                            rawInput = rawInput
                        )
                    ) {
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
                                    "Order already added: \"${result.invoiceNumber}\""
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
                                    "Order not found: \"${result.input}\""
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
                                    result.message
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


    // Extracted collectors from onStart()
    private suspend fun observeWorkOrder() {
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

    private suspend fun observeOrders() {
        workOrderIdFlow
            .flatMapLatest { id -> observeWorkOrderItemsInScanOrderUseCase(id) }
            .collectLatest { items ->
                setState { copy(collectOrderListItemStateList = items.toListItemState()) }
            }
    }

    private suspend fun observeSignature() {
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

    private suspend fun observeCollectionTypeGating() {
        workOrderIdFlow
            .flatMapLatest { id -> observeCollectionTypeGatingUseCase(id) }
            .collectLatest { gating ->
                val wasSelected = viewState.value.collectingType
                val newSelection = wasSelected?.takeIf { gating.isEnabled(it) }

                setState {
                    copy(
                        collectionTypeOptionList = collectionTypeOptionList.withEnabled(gating),
                        collectingType = newSelection
                    )
                }

                // Notify only when a previously set selection becomes invalid while some option remains enabled
                if (wasSelected != null && newSelection == null && gating.anyEnabled) {
                    setEffect {
                        OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                            message = "Selection cleared due to order mix change",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
    }

    // Helpers to keep gating logic cohesive and readable
    private fun CollectionTypeGating.isEnabled(type: CollectingType): Boolean = when (type) {
        CollectingType.STANDARD -> isStandardEnabled
        CollectingType.ACCOUNT -> isAccountEnabled
        CollectingType.COURIER -> isCourierEnabled
    }

    private val CollectionTypeGating.anyEnabled: Boolean
        get() = isStandardEnabled || isAccountEnabled || isCourierEnabled

    private fun List<CollectionTypeSectionDisplayState>.withEnabled(gating: CollectionTypeGating): List<CollectionTypeSectionDisplayState> {
        return map { option ->
            when (option.collectingType) {
                CollectingType.STANDARD -> option.copy(enabled = gating.isStandardEnabled)
                CollectingType.ACCOUNT -> option.copy(enabled = gating.isAccountEnabled)
                CollectingType.COURIER -> option.copy(enabled = gating.isCourierEnabled)
            }
        }
    }


    private suspend fun observeCohesiveGating() {
        val gatingFlow = workOrderIdFlow
            .flatMapLatest { id -> observeFulfilmentGatingUseCase(id) }

        val idVerificationFlow = viewState
            .map { state -> state.idVerified }
            .distinctUntilChanged()

        gatingFlow
            .combine(idVerificationFlow) { gating, idVerified ->
                val hasOrders = gating.hasOrders
                val type = gating.collectingType
                val isSignEnabled = when (type) {
                    CollectingType.STANDARD, CollectingType.ACCOUNT -> hasOrders && type != null && idVerified
                    CollectingType.COURIER -> hasOrders && type != null && !gating.courierName.isNullOrBlank()
                    null -> false
                }
                val isSubmitEnabled = isSignEnabled && gating.hasSignature
                val isCollectionTypeEnabled = hasOrders
                Triple(isCollectionTypeEnabled, isSignEnabled, isSubmitEnabled)
            }
            .collectLatest { (isCollectionTypeEnabled, isSignEnabled, isSubmitEnabled) ->
                setState {
                    copy(
                        isCollectionTypeEnabled = isCollectionTypeEnabled,
                        isSignEnabled = isSignEnabled,
                        isSubmitEnabled = isSubmitEnabled,
                    )
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
        val s = viewState.value
        if (!s.isSignEnabled) return
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
        if (!s.isSubmitEnabled) return
        val workOrderId = sessionState.value.workOrderId.handleNull() ?: return

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
                setEffect {
                    OrderFulfilmentScreenContract.Effect.ShowSnackbar(
                        "No active work order",
                        duration = SnackbarDuration.Long
                    )
                }
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
