package com.gpcasiapac.storesystems.common.feedback.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import co.touchlab.kermit.Logger

/**
 * Android implementation using Vibrator/VibratorManager.
 * Maps simple HapticEffect values to platform-predefined effects with sensible fallbacks.
 */
class AndroidHapticPerformer(
    private val context: Context,
    private val logger: Logger,
) : HapticPerformer {

    private val log = logger.withTag("HapticPerformer")

    @Suppress("DEPRECATION")
    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (t: Throwable) {
        log.e(t) { "Failed to get Vibrator service" }
        null
    }

    override fun perform(effect: HapticEffect) {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val ve = when (effect) {
                    HapticEffect.Error -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    HapticEffect.Success -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    HapticEffect.SelectionChanged -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                }
                v.vibrate(ve)
            } else {
                // Simple one-shot fallback (duration tuned per effect)
                @Suppress("DEPRECATION")
                val durationMs = when (effect) {
                    HapticEffect.Error -> 50
                    HapticEffect.Success -> 40
                    HapticEffect.SelectionChanged -> 20
                }
                @Suppress("DEPRECATION")
                v.vibrate(durationMs.toLong())
            }
        } catch (t: Throwable) {
            log.e(t) { "Failed to perform haptic: $effect" }
        }
    }
}
