package com.benoitfreslon.unity.vibrations.lib

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData
import com.benoitfreslon.unity.vibrations.lib.entities.VibrationResult
import com.benoitfreslon.unity.vibrations.lib.extensions.isEffectSupported
import com.benoitfreslon.unity.vibrations.lib.extensions.isPrimitiveSupported

@Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate")
open class Vibration(
    context: Context? = null,
    protected var vibrator: Vibrator? = null
) {

    var vibratorManager: VibratorManager? = null

    init {
        context?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

                if (vibrator == null) {
                    vibrator = vibratorManager?.defaultVibrator
                }
            } else if (vibrator == null) {
                vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }

    /**
     * Method overloading for call from Unity, passing C# enums like integer values
     */
    fun vibr(milliseconds: Long, valueType: Int, vibrationAttributes: VibrationAttributes? = null): VibrationResult {
        return vibr(milliseconds, VibrationType.getByValue(valueType), vibrationAttributes)
    }

    /**
     *
     * PS: The name of this method is "vibr()" to avoid conflicts with android core vibrator.vibrate()
     *     from Android JNI Unity bridge
     */
    fun vibr(milliseconds: Long, type: VibrationType? = null, vibrationAttributes: VibrationAttributes? = null): VibrationResult {

        var result = VibrationResult(success = true, type = VibrationResult.Type.OK)

        if (!hasVibrator()) {
            result = VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
            return result
        }

        /**
         * Default VibrationType here, to distinct from method vibrate(milliseconds, attributes) call.
         * If define this value on parameter "type", the compiler call the another method
         */
        val defaultType = VibrationType.SHORT
        val currentType = type ?: defaultType

        var hapticData = currentType.getData(vibrator, milliseconds, vibrationAttributes) ?: HapticData(vibrationAttributes = vibrationAttributes)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Fallback to Android O and P (8 and 9)
            if (hapticData.effect == null) {
                hapticData = defaultType.getData(vibrator, milliseconds, vibrationAttributes) ?: HapticData(vibrationAttributes = vibrationAttributes)

                if (hapticData.effect == null) {
                    result = VibrationResult(success = false, type = VibrationResult.Type.EFFECT_NOT_SUPPORT)
                    return result
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (hapticData.vibrationAttributes != null) {

                    vibrator?.vibrate(hapticData.effect!!, hapticData.vibrationAttributes!!)
                } else {
                    Log.e(TAG, "The parameter: 'vibrationAttributes' is required for Android SDK >= ${Build.VERSION_CODES.TIRAMISU}")

                    vibrator?.vibrate(hapticData.effect, hapticData.attributes)
                    result = VibrationResult(success = false, type = VibrationResult.Type.ATTRIBUTES_MISSING)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vibratorManager?.vibrate(
                    CombinedVibration.createParallel(hapticData.effect!!),
                    hapticData.vibrationAttributes
                )
            } else {
                vibrator?.vibrate(hapticData.effect, hapticData.attributes)
            }

        } else {
            vibr(milliseconds, hapticData.attributes!!)
        }

        Log.i(TAG, "Vibration with result: '$result'")
        return result
    }

    fun vibr(milliseconds: Long, type: VibrationType? = null, attributes: AudioAttributes): VibrationResult? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val vibrationAttributes = VibrationAttributes.Builder(attributes).build()
            vibr(milliseconds, type, vibrationAttributes)
        } else null
    }

    /**
     * TODO: Add a parameter to pass a map or data class to pass properties:
     *       - wait: A array of Long values with a time to WAIT before each vibrate, in milliseconds
     *       - vibrate: A array of Long values with a time to VIBRATE after each wait value, in milliseconds
     */
    fun vibr(pattern: LongArray, repeat: Int = -1, vibrationAttributes: VibrationAttributes? = null): VibrationResult {

        if (!hasVibrator()) {
            return VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
        }
        
        val hapticData = HapticData(vibrationAttributes = vibrationAttributes)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator?.vibrate(pattern, repeat, hapticData.attributes)

            VibrationResult(success = true, type = VibrationResult.Type.OK)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hapticData.vibrationAttributes != null) {
                val effect = VibrationEffect.createWaveform(pattern, repeat)
                vibrator?.vibrate(effect, hapticData.vibrationAttributes!!)

                VibrationResult(success = true, type = VibrationResult.Type.OK)
            } else {
                Log.e(TAG, "The parameter: 'vibrationAttributes' is required for Android SDK >= ${Build.VERSION_CODES.TIRAMISU}")
                return VibrationResult(success = false, type = VibrationResult.Type.ATTRIBUTES_MISSING)
            }

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, repeat)
            vibrator?.vibrate(effect, hapticData.attributes)

            VibrationResult(success = true, type = VibrationResult.Type.OK)
        } else {
            VibrationResult(success = false, type = VibrationResult.Type.PATTERN_NOT_SUPPORT)
        }
    }

    @Suppress("unused")
    fun vibr(pattern: LongArray, repeat: VibrationRepeat = VibrationRepeat.NO_REPEAT, vibrationAttributes: VibrationAttributes? = null): VibrationResult {
        return vibr(pattern, repeat.value, vibrationAttributes)
    }

    fun hasVibrator() = vibrator?.hasVibrator() == true

    @Suppress("unused")
    fun isEffectSupported(effectId: Int) = vibrator.isEffectSupported(effectId)
    @Suppress("unused")
    fun isPrimitiveSupported(primitiveId: Int) = vibrator.isPrimitiveSupported(primitiveId)

    protected open fun vibr(milliseconds: Long, attributes: AudioAttributes): VibrationResult {
        
        if (!hasVibrator()) {
            return VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
        }
        
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator?.vibrate(milliseconds)
            VibrationResult(success = true, type = VibrationResult.Type.OK)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val hapticData = HapticData()
            vibrator?.vibrate(milliseconds, hapticData.attributes)
    
            VibrationResult(success = true, type = VibrationResult.Type.OK)
        } else {
    
            // Fallback to Android.O vibrate() function, if someone call this method directly
           vibr(milliseconds, VibrationType.SHORT, attributes)
           VibrationResult(success = true, type = VibrationResult.Type.OK)
        }
    }

    companion object {
        const val TAG = "VibrationPlugin"
    }
}
