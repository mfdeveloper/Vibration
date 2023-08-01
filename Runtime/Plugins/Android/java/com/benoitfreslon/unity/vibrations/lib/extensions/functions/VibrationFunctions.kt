package com.benoitfreslon.unity.vibrations.lib.extensions.functions

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.benoitfreslon.unity.vibrations.lib.entities.HapticPattern

@JvmOverloads
fun predefinedEffect(
    vibrator: Vibrator? = null,
    id: Int? = null,
    fallbackDuration: Long? = null,
    patternData: HapticPattern? = null
): VibrationEffect? {

    return if (id != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        VibrationEffect.createPredefined(id)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (fallbackDuration != null && fallbackDuration > 0) {
            VibrationEffect.createOneShot(fallbackDuration, VibrationEffect.DEFAULT_AMPLITUDE)
        } else if (patternData?.amplitude != null) {
            if (vibrator != null && !vibrator.hasAmplitudeControl()) {
                VibrationEffect.createWaveform(patternData.pattern, patternData.repeat)
            } else {
                VibrationEffect.createWaveform(
                    patternData.pattern,
                    patternData.amplitude,
                    patternData.repeat
                )
            }

        } else if (patternData?.pattern != null) {
            VibrationEffect.createWaveform(patternData.pattern, patternData.repeat)
        } else null

    } else null
}

/**
 * Use [VibrationEffect.startComposition] to create a [VibrationEffect] and pass through
 * [android.os.VibratorManager.vibrate] or [android.os.Vibrator.vibrate]
 *
 * ## References
 *
 * - [Android 12: VibratorManager & New Vibration Primitives](https://yggr.medium.com/exploring-android-12-vibratormanager-new-vibration-primitives-e862c95fe938)
 */
@JvmOverloads
fun predefinedPrimitive(
    vibrator: Vibrator? = null,
    primitiveId: Int?,
    fallbackDuration: Long? = null,
    patternData: HapticPattern? = null
): VibrationEffect? {
    if (primitiveId == null) {
        return null
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        VibrationEffect.startComposition()
            .addPrimitive(primitiveId)
            .compose()
    } else predefinedEffect(vibrator, primitiveId, fallbackDuration, patternData)
}
