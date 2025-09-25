package com.gpcasiapac.storesystems.feature.login.presentation.otp_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MfaScreen(
    userId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOtpSuccess: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("OTP screen for $userId (stub)")
        Button(onClick = onBack) { Text("Back") }
        Button(onClick = onOtpSuccess) { Text("Confirm OTP (stub)") }
    }
}
