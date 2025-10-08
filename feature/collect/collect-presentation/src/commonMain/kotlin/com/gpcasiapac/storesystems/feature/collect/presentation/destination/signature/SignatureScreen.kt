package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.SignatureInput
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignatureCanvas
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    state: SignatureScreenContract.State,
    onEventSent: (event: SignatureScreenContract.Event) -> Unit,
    effectFlow: Flow<SignatureScreenContract.Effect>,
    onOutcome: (outcome: SignatureScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val signatureBitmap = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is SignatureScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )

                is SignatureScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)

                is SignatureScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = {
                    TopBarTitle("Signature")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEventSent(SignatureScreenContract.Event.Back)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(Dimens.Space.medium),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
            ) {
                // Customer Details Card
                CustomerDetails(
                    customerName = "Customer Name",
                    customerNumber = "Customer Number",
                    phoneNumber = "Phone Number",
                    customerType = CustomerType.B2C
                )
                HorizontalDivider()
//                SignatureInput {
//                    signatureBitmap.value = it
//                }
                // Signature Canvas
                //Signature
                SignatureCanvas(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .weight(1f)  ,
                    strokes = state.signatureStrokes,
                    onStrokesChange = { strokes ->
                        onEventSent(SignatureScreenContract.Event.StrokesChanged(strokes))
                    },
                    strokeWidth = 6.dp,
                    strokeColor = MaterialTheme.colorScheme.primary,
                )

                // Confirm Button
                Button(
                    onClick = {
                        if (state.signatureStrokes.isNotEmpty()) {
                            onEventSent(SignatureScreenContract.Event.StartCapture)
                        }
                    },
                    enabled = !state.isLoading && state.signatureStrokes.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.Size.buttonHeight),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "SAVE SIGNATURE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

//    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
//        Content(padding, state, onEventSent)
//    }
}

