package com.benoitfreslon.unity.vibrations.lib.extended

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.benoitfreslon.unity.vibrations.lib.Vibration
import com.benoitfreslon.unity.vibrations.lib.entities.HapticPattern
import com.benoitfreslon.unity.vibrations.lib.entities.VibrationResult
import com.benoitfreslon.unity.vibrations.lib.enums.VibrationRepeat
import com.benoitfreslon.unity.vibrations.lib.enums.VibrationType
import com.benoitfreslon.unity.vibrations.lib.extended.entities.HapticDataExtended
import com.benoitfreslon.unity.vibrations.lib.extended.enums.VibrationTypeExtended
import com.benoitfreslon.unity.vibrations.lib.extensions.convertEnum
import com.benoitfreslon.unity.vibrations.lib.extensions.toAudioAttrs
import com.benoitfreslon.unity.vibrations.lib.extensions.toVibrationAttrs

/**
 * Use this class only for latest vibration APIs Android SDK 30+ version, such as [VibrationAttributes] and [VibratorManager]
 *
 * **PS:** Pay attention that Unity <= `2022.3´ don't support these classes by default, even you configure the
 * target Android API to 30+. You will always get the runtime exception [NoClassDefFoundError]. That error probably happens
 * because of Gradle version that Unity supports.
 *
 * ## References
 *
 * - [Android 12: VibratorManager & New Vibration Primitives](https://yggr.medium.com/exploring-android-12-vibratormanager-new-vibration-primitives-e862c95fe938)
 * - [Unity: Gradle for Android](https://docs.unity3d.com/Manual/android-gradle-overview.html)
 */
@Suppress("unused")
open class VibrationExtended(
    context: Context? = null,
    vibrator: Vibrator? = null
) : Vibration(context, vibrator) {

    protected open var vibratorManager: VibratorManager? = null

    init {
        context?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

                if (this.vibrator == null) {
                    this.vibrator = vibratorManager?.defaultVibrator
                }
            } else if (vibrator == null) {
                @Suppress("DEPRECATION")
                this.vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }

    @JvmOverloads
    fun vibrate(milliseconds: Long? = null, type: VibrationTypeExtended? = null, vibrationAttributes: VibrationAttributes): VibrationResult {

        if (!hasVibrator()) {
            return VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
        }

        if (milliseconds == null && type == null) {
            Log.e(TAG, "Time value 'milliseconds' parameter is required!")
            return VibrationResult(success = false, type = VibrationResult.Type.DURATION_OR_TYPE_REQUIRED)
        }

        val result = VibrationResult(success = true, type = VibrationResult.Type.OK)

        // TODO: [Refactor] Move that condition and the code above to the parent class [Vibration]
        //       in order to reuse and avoid duplicated code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && type != null) {

            val hapticData = type.getData(vibrator, milliseconds, vibrationAttributes) ?: HapticDataExtended(vibrationAttributes = vibrationAttributes)

            if (hapticData.effect == null) {
                return VibrationResult(success = false, type = VibrationResult.Type.EFFECT_NOT_SUPPORT)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (hapticData.vibrationAttributes != null) {

                    vibrator?.vibrate(hapticData.effect!!, hapticData.vibrationAttributes!!)
                } else {
                    val msg = "The parameter: 'vibrationAttributes' is required for Android SDK >= ${Build.VERSION_CODES.TIRAMISU}." +
                            " Fallback to use AudioAttributes instead"

                    Log.w(TAG, msg)

                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(hapticData.effect, hapticData.audioAttributes)
                    result.type = VibrationResult.Type.ATTRIBUTES_MISSING
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vibratorManager?.vibrate(
                    CombinedVibration.createParallel(hapticData.effect!!),
                    hapticData.vibrationAttributes
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(hapticData.effect, hapticData.audioAttributes)
            }

            result.vibrationTypeExtended = type

        } else {
            super.vibrate(
                milliseconds,
                type = type?.convertEnum<VibrationTypeExtended, VibrationType>(),
                audioAttributes = vibrationAttributes.toAudioAttrs()
            )
        }

        Log.i(TAG, "Vibration with result: '$result'")
        return result
    }

    /**
     * TODO: Add a parameter to pass a map or data class to pass properties:
     *       - wait: A array of Long values with a time to WAIT before each vibrate, in milliseconds
     *       - vibrate: A array of Long values with a time to VIBRATE after each wait value, in milliseconds
     */
    fun vibrate(pattern: LongArray, repeat: Int = -1, vibrationAttributes: VibrationAttributes): VibrationResult {

        if (!hasVibrator()) {
            return VibrationResult(success = false, type = VibrationResult.Type.VIBRATOR_NOT_SUPPORT)
        }

        val hapticData = HapticDataExtended(vibrationAttributes = vibrationAttributes)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, repeat, hapticData.audioAttributes)

            VibrationResult(
                success = true,
                type = VibrationResult.Type.OK,
                patternData = HapticPattern(pattern = pattern, repeat = repeat)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val effect = VibrationEffect.createWaveform(pattern, repeat)

            if (hapticData.vibrationAttributes != null) {
                vibrator?.vibrate(effect, hapticData.vibrationAttributes!!)

                VibrationResult(
                    success = true,
                    type = VibrationResult.Type.OK,
                    patternData = HapticPattern(pattern = pattern, repeat = repeat)
                )
            } else {
                val msg = "The parameter: 'vibrationAttributes' is required for Android SDK >= ${Build.VERSION_CODES.TIRAMISU}." +
                        " Fallback to use 'AudioAttributes'"

                Log.w(TAG, msg)

                @Suppress("DEPRECATION")
                vibrator?.vibrate(effect, hapticData.audioAttributes)

                VibrationResult(
                    success = true,
                    type = VibrationResult.Type.ATTRIBUTES_MISSING,
                    patternData = HapticPattern(pattern = pattern, repeat = repeat)
                )
            }

        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, repeat)

            @Suppress("DEPRECATION")
            vibrator?.vibrate(effect, hapticData.audioAttributes)

            VibrationResult(
                success = true,
                type = VibrationResult.Type.OK,
                patternData = HapticPattern(pattern = pattern, repeat = repeat)
            )
        } else {
            VibrationResult(
                success = false,
                type = VibrationResult.Type.PATTERN_NOT_SUPPORT,
                patternData = HapticPattern(pattern = pattern, repeat = repeat)
            )
        }
    }

    @Suppress("unused")
    fun vibrate(
        pattern: LongArray,
        repeat: VibrationRepeat = VibrationRepeat.NO_REPEAT,
        vibrationAttributes: VibrationAttributes
    ): VibrationResult {
        return vibrate(pattern, repeat = repeat.value, vibrationAttributes)
    }

    override fun vibrate(
        milliseconds: Long?,
        type: VibrationType?,
        audioAttributes: AudioAttributes?
    ): VibrationResult {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val typeExtended = type?.convertEnum<VibrationType, VibrationTypeExtended>()
            vibrate(milliseconds, typeExtended, vibrationAttributes = audioAttributes.toVibrationAttrs())
        } else super.vibrate(milliseconds, type, audioAttributes)
    }

    @Suppress("unused")
    override fun cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && hasVibrator()) {
            vibratorManager?.cancel()
        } else {
            super.cancel()
        }
    }
}
