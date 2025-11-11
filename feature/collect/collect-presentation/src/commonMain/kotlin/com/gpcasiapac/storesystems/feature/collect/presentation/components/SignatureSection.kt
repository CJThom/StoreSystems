package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.foundation.design_system.Dimens

@Composable
fun SignatureSection(
    onSignClick: () -> Unit,
    signatureStrokes: List<List<Offset>>,
    modifier: Modifier = Modifier,
    onRetakeClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {
    val hasSignature = signatureStrokes.isNotEmpty()
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        Text(
            text = "Signature",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(Dimens.Space.small)
        ) {
            if (hasSignature) {
                Column {
                    // Show signature preview
                    SignaturePreview(
                        strokes = signatureStrokes,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Buttons row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
                    ) {
                        OutlinedButton(
                            onClick = onRetakeClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(0.dp, 0.dp, Dimens.Space.small, 0.dp)
                        ) {
                            Text("RETAKE")
                        }
                        Button(
                            onClick = onSignClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, Dimens.Space.small),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("VIEW")
                        }
                    }
                }
            } else {
                // Show sign button when no signature
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    border = MaterialTheme.borderStroke()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = onSignClick
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit"
                            )
                            Text(text = "SIGN")
                        }
                    }
                }

            }
        }
    }
}
