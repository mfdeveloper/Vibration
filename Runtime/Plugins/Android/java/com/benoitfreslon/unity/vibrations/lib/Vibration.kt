package com.benoitfreslon.unity.vibrations.lib

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData
import com.benoitfreslon.unity.vibrations.lib.entities.HapticPattern
import com.benoitfreslon.unity.vibrations.lib.entities.VibrationResult
import com.benoitfreslon.unity.vibrations.lib.enums.VibrationRepeat
import com.benoitfreslon.unity.vibrations.lib.enums.VibrationType
import com.benoitfreslon.unity.vibrations.lib.extensions.getByValue
import com.benoitfreslon.unity.vibrations.lib.extensions.isEffectSupported
import com.benoitfreslon.unity.vibrations.lib.extensions.isPrimitiveSupported
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate")

/**
 * **PS:** Pay attention that isn't possible use the new annotation **`androidx.annotation.RequiresApi`** on Unity (<= _2022.3.1_)
 * You will see the error *"Unresolved reference: RequiresApi"* when build for Android platform fro Unity Editor :(
 *
 * Probably, you must use [googlesamples/unity-jar-resolver](https://github.com/googlesamples/unity-jar-resolver) Google plugin in order to
 * add the dependency: `androidx.core:core-ktx:1.10.1`, for instance.
 *
 * If you need restrict any method to an specific Android API, use [android.annotation.TargetApi] annotation instead
 */
open class Vibration @JvmOverloads constructor(
    context: Context? = null,
    protected var vibrator: Vibrator? = null
) {

    init {
        context?.let {
            if (vibrator == null) {
                @Suppress("DEPRECATION")
                vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                HapticData.vibrator = vibrator
            }
        }
    }

    @JvmOverloads
    constructor(
        contextWrapper: ContextWrapper,
        vibrator: Vibrator? = null
    ) : this(
        contextWrapper.applicationContext,
        vibrator
    )

    @JvmOverloads
    @Suppress("unused")
    fun vibrate(milliseconds: Long? = null, valueType: Int, audioAttributes: AudioAttributes? = null): VibrationResult {
        return vibrate(milliseconds, Enum.getByValue<VibrationType>(valueType), audioAttributes)
    }

    /**
     * Use this overload method if you prefer call this from Unity with a primitive **`int`** type [milliseconds] parameter, instead of
     * instantiate a `new AndroidJavaObject("java.lang.Long")" from `C#`.
     */
    @JvmOverloads
    @Suppress("unused")
    fun vibrate(milliseconds: Int, valueType: Int, audioAttributes: AudioAttributes? = null): VibrationResult {
        return vibrate(milliseconds.toLong(), Enum.getByValue<VibrationType>(valueType), audioAttributes)
    }

    @JvmOverloads
    open fun vibrate(milliseconds: Long? = null, type: VibrationType? = null, audioAttributes: AudioAttributes? = null): VibrationResult {

        if (!hasVibrator()) {
            return VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
        }

        if (milliseconds == null && type == null) {
            Log.e(TAG, "Time value 'milliseconds' or ${VibrationType::class.qualifiedName} 'type' parameter is required!")
            return VibrationResult(success = false, type = VibrationResult.Type.DURATION_OR_TYPE_REQUIRED)
        }

        var duration = milliseconds
        var patternData: HapticPattern? = null
        var hapticData = HapticData(audioAttributes = audioAttributes)

        var result = VibrationResult(success = true, type = VibrationResult.Type.OK)
        val resultSuccessMsg = "Vibration with result: '%s'"

        if (type != null) {
            hapticData = type.getData(vibrator, duration, audioAttributes) ?: hapticData
            result.vibrationType = type

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                if (hapticData.isEmpty) {
                    return VibrationResult(
                        success = false,
                        type = VibrationResult.Type.EFFECT_NOT_SUPPORT,
                        vibrationType = type
                    )
                } else if (hapticData.effect != null) {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(hapticData.effect, hapticData.audioAttributes)

                    Log.i(TAG, String.format(resultSuccessMsg, result))
                    return result
                }
            }

            if (hapticData.effect == null) {
                if (hapticData.fallbackDuration != null) {
                    duration = hapticData.fallbackDuration
                }

                if (hapticData.patternData != null) {
                    patternData = hapticData.patternData
                }
            }
        }

        if (patternData != null) {

            result = vibrate(patternData.pattern, patternData.repeat, hapticData.audioAttributes)

        } else if (duration != null && duration > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration, hapticData.audioAttributes)

            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(duration)
            }

            result.duration = duration
        }

        Log.i(TAG, String.format(resultSuccessMsg, result))
        return result
    }

    @Suppress("unused")
    fun vibrate(duration: Long, timeUnit: TimeUnit): VibrationResult {
        val milliseconds = if (timeUnit != TimeUnit.MILLISECONDS) {
             timeUnit.toMillis(duration)
        } else duration

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vibrate(milliseconds)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(milliseconds)

            val result = VibrationResult(
                success = true,
                type = VibrationResult.Type.OK,
                duration = milliseconds
            )

            Log.i(TAG, "Vibration with result: '$result'")

            result
        }
    }

    @Suppress("unused")
    fun vibrateWithTimeUnit(duration: Long, timeUnitValue: String = TimeUnit.SECONDS.name): VibrationResult {
        val timeUnit = enumValueOf<TimeUnit>(timeUnitValue)
        return vibrate(duration, timeUnit)
    }

    /**
     * TODO: Add a parameter to pass a map or data class to pass properties:
     *       - wait: A array of Long values with a time to WAIT before each vibrate, in milliseconds
     *       - vibrate: A array of Long values with a time to VIBRATE after each wait value, in milliseconds
     */
    @JvmOverloads
    fun vibrate(pattern: LongArray, repeat: Int = -1, audioAttributes: AudioAttributes? = null): VibrationResult {

        if (!hasVibrator()) {
            return VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
        }

        val hapticData = HapticData(
            audioAttributes = audioAttributes,
            patternData = HapticPattern(pattern = pattern, repeat = repeat)
        )

        val result = VibrationResult(
            success = true,
            type = VibrationResult.Type.OK,
            patternData = hapticData.patternData
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, repeat, hapticData.audioAttributes)

            result
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Fallback to previously created HapticData.effect from pattern
            val effect = hapticData.effect ?: VibrationEffect.createWaveform(pattern, repeat)

            @Suppress("DEPRECATION")
            vibrator?.vibrate(effect, hapticData.audioAttributes)

            result
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, repeat)

            result
        }
    }

    fun vibrate(pattern: LongArray, repeat: Int = -1, hapticData: HapticData): VibrationResult {
        return vibrate(pattern, repeat, hapticData.audioAttributes)
    }

    @Suppress("unused")
    fun vibrate(pattern: LongArray, repeat: VibrationRepeat = VibrationRepeat.NO_REPEAT, audioAttributes: AudioAttributes? = null): VibrationResult {
        return vibrate(pattern, repeat = repeat.value, audioAttributes)
    }

    fun hasVibrator() = vibrator?.hasVibrator() == true

    @Suppress("unused")
    fun isEffectSupported(effectId: Int) = vibrator.isEffectSupported(effectId)
    @Suppress("unused")
    fun isPrimitiveSupported(primitiveId: Int) = vibrator.isPrimitiveSupported(primitiveId)

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.O)
    fun hasAmplitudeControl() = vibrator?.hasAmplitudeControl() == true

    @Suppress("unused")
    open fun cancel() {
        if (hasVibrator()) {
            vibrator?.cancel()
        }
    }

    companion object {

        @JvmStatic
        val TAG: String = "${Vibration::class.simpleName}Plugin"
    }
}
