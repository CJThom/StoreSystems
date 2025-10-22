package com.gpcasiapac.storesystems.common.scanning

/** Platform-specific implementation is bound in DI. */
interface ScannerFactory { fun create(): Scanner }