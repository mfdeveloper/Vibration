package com.benoitfreslon.unity.vibrations.lib

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

enum class VibrationRepeat(val value: Int) {
    NO_REPEAT(-1), FOREVER(0)
}

open class Vibration(context: Context? = null) {

    lateinit var vibrator: Vibrator
    private set

    init {
        if (context != null) {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    constructor(vibrator: Vibrator): this() {
        this.vibrator = vibrator
    }

    /**
     * Method overloading for call from Unity, passing C# enums like integer values
     */
    fun vibr(milliseconds: Long, valueType: Int, attributes: AudioAttributes? = null): VibrationResult {
        return vibr(milliseconds, VibrationType.getByValue(valueType), attributes)
    }

    /**
     * TODO: Use methods like VibrationEffect.startComposition() to more flexible vibrations HAPTICS
     *       This is only for next Android.R 11 version (probably will be API 30)
     *
     * PS: The name of this method is "vibr()" to avoid conflicts with android core vibrator.vibrate()
     *     from Android JNI Unity bridge
     */
    fun vibr(milliseconds: Long, type: VibrationType? = null, attributes: AudioAttributes? = null): VibrationResult {

        var result = VibrationResult(success = true, type = VibrationResult.Type.OK)

        /**
         * Default VibrationType here, to distinct from method vibrate(milliseconds, attributes) call.
         * If define this value on parameter "type", the compiler call the another method
         */
        val defaultType = VibrationType.SHORT
        val currentType = type ?: defaultType

        if (vibrator.hasVibrator()) {

            var hapticData: HapticData? = currentType.getData(milliseconds, attributes) ?: HapticData(attributes = attributes)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                // Fallback to Android O and P (8 and 9)
                if (hapticData?.effect == null) {
                    hapticData = defaultType.getData(milliseconds, attributes)
                    result = VibrationResult(success = false, type = VibrationResult.Type.EFFECT_NOT_SUPPORT)
                }

                vibrator.vibrate(hapticData?.effect, hapticData?.attributes)

            } else {
                vibr(milliseconds, hapticData?.attributes!!)
            }
        }

        return result
    }

    /**
     * TODO: Add a parameter to pass a map or data class to pass a properties:
     *       - wait: A array of Long values with a time to WAIT before each vibrate, in milliseconds
     *       - vibrate: A array of Long values with a time to VIBRATE after each wait value, in milliseconds
     */
    fun vibr(pattern: LongArray, repeat: Int = -1, attributes: AudioAttributes? = null): VibrationResult {

        if (vibrator.hasVibrator()) {

            val hapticData = HapticData(attributes = attributes)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                vibrator.vibrate(pattern, repeat, hapticData.attributes)

                VibrationResult(success = true, type = VibrationResult.Type.OK)
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, repeat)

                vibrator.vibrate(effect, hapticData.attributes)

                VibrationResult(success = true, type = VibrationResult.Type.OK)
            } else {
                VibrationResult(success = false, type = VibrationResult.Type.PATTERN_NOT_SUPPORT)
            }
        }

        return VibrationResult(success = false)
    }

    fun vibr(pattern: LongArray, repeat: VibrationRepeat = VibrationRepeat.NO_REPEAT, attributes: AudioAttributes? = null): VibrationResult {
        return vibr(pattern, repeat.value, attributes)
    }

    protected open fun vibr(milliseconds: Long, attributes: AudioAttributes): VibrationResult {

       return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrator.vibrate(milliseconds)
            VibrationResult(success = true, type = VibrationResult.Type.OK)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val hapticData = HapticData()
            vibrator.vibrate(milliseconds, hapticData.attributes)

            VibrationResult(success = true, type = VibrationResult.Type.OK)
        } else {

            // Fallback to Android.O vibrate() function, if someone call this method directly
           vibr(milliseconds, VibrationType.SHORT, attributes)
        }
    }
}