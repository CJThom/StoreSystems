package com.gpcasiapac.storesystems.common.scanning.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import co.touchlab.kermit.Logger

/**
 * Encapsulates creation and update of the DataWedge profile.
 * Splits configuration into BARCODE, INTENT and KEYSTROKE steps, then switches to the profile.
 */
internal class DataWedgeProfileManager(
    private val context: Context,
    private val profileName: String,
    private val intentAction: String,
    private val log: Logger,
) {
    fun ensureProfile() {
        log.i { "DW ensureProfile(): '$profileName' action='$intentAction'" }
        // Create / idempotent
        DataWedgeIntents.sendCommand(context, DataWedgeIntents.EXTRA_CREATE_PROFILE, profileName)
        // Configure plugins
        setBarcode()
        setIntent()
        disableKeystroke()
        // Activate
        DataWedgeIntents.sendCommand(context, DataWedgeIntents.EXTRA_SWITCH_TO_PROFILE, profileName)
        log.d { "DW switched active profile to '$profileName'" }
    }

    private fun baseProfileBundle(): Bundle = Bundle().apply {
        putString("PROFILE_NAME", profileName)
        putString("PROFILE_ENABLED", "true")
        putString("CONFIG_MODE", "UPDATE")
        putParcelableArray("APP_LIST", arrayOf(Bundle().apply {
            putString("PACKAGE_NAME", context.packageName)
            putStringArray("ACTIVITY_LIST", arrayOf("*"))
        }))
    }

    private fun setBarcode() {
        val bundle = baseProfileBundle().apply {
            putBundle("PLUGIN_CONFIG", Bundle().apply {
                putString("PLUGIN_NAME", "BARCODE")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("configure_all_scanners", "true")
                    putString("scanner_input_enabled", "true")
                    // Enable a minimal set of common decoders; extend if needed
                    putString("decoder_code128", "true")
                    putString("decoder_ean13", "true")
                    putString("decoder_ean8", "true")
                    putString("decoder_qr_code", "true")
                })
            })
        }
        log.d { "DW SET_CONFIG: BARCODE for '$profileName'" }
        DataWedgeIntents.sendBundle(context, DataWedgeIntents.EXTRA_SET_CONFIG, bundle)
    }

    private fun setIntent() {
        val bundle = baseProfileBundle().apply {
            putBundle("PLUGIN_CONFIG", Bundle().apply {
                putString("PLUGIN_NAME", "INTENT")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("intent_output_enabled", "true")
                    putString("intent_action", intentAction)
                    putString("intent_delivery", "2") // Broadcast
                    putString("intent_category", Intent.CATEGORY_DEFAULT)
                    putString("intent_use_content_provider", "false")
                })
            })
        }
        log.d { "DW SET_CONFIG: INTENT for '$profileName', action='$intentAction'" }
        DataWedgeIntents.sendBundle(context, DataWedgeIntents.EXTRA_SET_CONFIG, bundle)
    }

    private fun disableKeystroke() {
        val bundle = baseProfileBundle().apply {
            putBundle("PLUGIN_CONFIG", Bundle().apply {
                putString("PLUGIN_NAME", "KEYSTROKE")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("keystroke_output_enabled", "false")
                })
            })
        }
        log.d { "DW SET_CONFIG: KEYSTROKE disable for '$profileName'" }
        DataWedgeIntents.sendBundle(context, DataWedgeIntents.EXTRA_SET_CONFIG, bundle)
    }
}
