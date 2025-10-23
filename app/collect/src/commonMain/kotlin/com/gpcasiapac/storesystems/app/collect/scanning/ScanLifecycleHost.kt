package com.gpcasiapac.storesystems.app.collect.scanning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.ScanEventsRegistry
import com.gpcasiapac.storesystems.common.scanning.Scanner
import org.koin.compose.koinInject

/**
 * Cross-platform scanner lifecycle host. Resolves Scanner from DI after
 * KoinMultiplatformApplication has started and wires ScanEventsRegistry so
 * common UI can observe scans. Starts/stops the scanner with composition.
 */
@Composable
fun ScanLifecycleHost() {
    val logger: Logger = koinInject()
    val scanner: Scanner = koinInject()
    val log = logger.withTag("ScanLifecycleHost")

    // Start/stop with composition lifecycle
    DisposableEffect(scanner) {
        log.i { "Starting scanner" }
        runCatching { scanner.start() }
            .onFailure { t -> log.e(t) { "Failed to start scanner" } }
        onDispose {
            log.i { "Stopping scanner" }
            runCatching { scanner.stop() }
                .onFailure { t -> log.e(t) { "Failed to stop scanner" } }
        }
    }

    // Expose scans to common UI via registry
    LaunchedEffect(scanner) {
        log.i { "Wiring ScanEventsRegistry.provider" }
        ScanEventsRegistry.provider = { scanner.scans }
    }
}
