package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.components.DrawCanvas
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.component.SignatureOrderSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow

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
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(Dimens.Space.medium),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//
//                    ) {
//                    Text(
//                        text = state.customerName,
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                    Spacer(modifier = Modifier.size(Dimens.Space.medium))
//                    Text(
//                        text = "To collect",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }

                // Order details summary
                SignatureOrderSummary(
                    orders = state.selectedOrderList,
                    onViewDetails = { onEventSent(SignatureScreenContract.Event.ViewDetailsClicked) },
                    modifier = Modifier
                )
                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Space.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Please sign here",
                        style = MaterialTheme.typography.titleLarge
                    )
                    OutlinedButton(
                        enabled = state.signatureStrokes.isNotEmpty(),
                        onClick = {
                            onEventSent(SignatureScreenContract.Event.ClearSignature)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                            Text("Clear All")
                        }
                    }
                }

                DrawCanvas(
                    onComplete = { image ->
                        onEventSent(SignatureScreenContract.Event.SignatureCompleted(image))
                    },
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    strokes = state.signatureStrokes,
                    onStrokesChange = { strokes ->
                        onEventSent(SignatureScreenContract.Event.StrokesChanged(strokes))
                    },
                    strokeColor = Color.Black,
                )

                // Confirm Button
                Button(
                    onClick = {
                        if (state.signatureBitmap != null) {
                            onEventSent(SignatureScreenContract.Event.StartCapture)
                        }
                    },
                    enabled = !state.isLoading && state.signatureBitmap != null,
                    modifier = Modifier
                        .padding(Dimens.Space.medium)
                        .fillMaxWidth(),
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
}


@Preview(
    name = "Signature",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun SignatureScreenPreview(
    @PreviewParameter(SignatureScreenStateProvider::class) state: SignatureScreenContract.State
) {
    GPCTheme {
        SignatureScreen(
            state = state,
            onEventSent = {},
            effectFlow = emptyFlow(),
            onOutcome = {}
        )
    }
}
