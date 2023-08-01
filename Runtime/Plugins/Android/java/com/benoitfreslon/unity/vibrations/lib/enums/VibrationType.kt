package com.benoitfreslon.unity.vibrations.lib.enums

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData
import com.benoitfreslon.unity.vibrations.lib.entities.HapticPattern
import com.benoitfreslon.unity.vibrations.lib.extensions.isEffectSupported
import com.benoitfreslon.unity.vibrations.lib.extensions.isPrimitiveSupported
import kotlin.reflect.KProperty0

// TODO: Add a new method into [VibrationType] enum to allow add custom effect types from Unity `C#`
enum class VibrationType(
    val fallbackDuration: Long? = null,
    val patternData: HapticPattern? = null,
    var defaultType: VibrationType? = VibrationType.allDefaultType
) : IVibrationType {

    TICK(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 5),
            amplitude = intArrayOf(0, 50)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ) = createDataForEffect(
            vibrator = vibrator,
            defaultType = defaultType,
            patternData = patternData,
            effectIdReference = VibrationEffect::EFFECT_TICK,
            audioAttributes = audioAttributes
        )
    },

    LOW_TICK(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 100, 50),
            amplitude = intArrayOf(30, 0, 30)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ) = createDataForPrimitive(
            vibrator = vibrator,
            defaultType = defaultType,
            milliseconds = milliseconds ?: fallbackDuration,
            primitiveIdReference = VibrationEffect.Composition::PRIMITIVE_LOW_TICK,
            audioAttributes = audioAttributes
        )
    },

    SHORT(fallbackDuration = 20) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            return TICK.getData(
                vibrator,
                milliseconds ?: fallbackDuration,
                audioAttributes
            )
        }
    },

    LIGHT {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            return TICK.getData(
                vibrator,
                milliseconds ?: fallbackDuration,
                audioAttributes
            )
        }
    },

    NORMAL(fallbackDuration = 1000) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ) = createDataForEffect(
            vibrator = vibrator,
            defaultType = defaultType,
            milliseconds = milliseconds ?: fallbackDuration,
            effectIdReference = VibrationEffect::EFFECT_TICK,
            audioAttributes = audioAttributes
        )
    },

    CLICK(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 10),
            amplitude = intArrayOf(0, 180)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ) = createDataForEffect(
            vibrator = vibrator,
            defaultType = defaultType,
            patternData = patternData,
            milliseconds = milliseconds ?: fallbackDuration,
            effectIdReference = VibrationEffect::EFFECT_CLICK,
            audioAttributes = audioAttributes
        )
    },

    DOUBLE_TAP(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 75, 75, 75)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ) = createDataForEffect(
            vibrator = vibrator,
            defaultType = defaultType,
            patternData = patternData,
            effectIdReference = VibrationEffect::EFFECT_DOUBLE_CLICK,
            audioAttributes = audioAttributes
        )
    },

    DOUBLE_CLICK {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            return DOUBLE_TAP.getData(
                vibrator,
                milliseconds ?: fallbackDuration,
                audioAttributes
            )
        }
    },

    HEAVY(
        fallbackDuration = 200,
        patternData = HapticPattern(
            pattern = longArrayOf(0, 30, 50),
            amplitude = intArrayOf(255, 0, 255)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ) = createDataForEffect(
            vibrator = vibrator,
            defaultType = defaultType,
            patternData = patternData,
            effectIdReference = VibrationEffect::EFFECT_HEAVY_CLICK,
            milliseconds = milliseconds ?: fallbackDuration,
            audioAttributes = audioAttributes
        )
    },

    THUD(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 100),
            amplitude = intArrayOf(255, 0)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {

            val primitiveIdReference = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                VibrationEffect.Composition::PRIMITIVE_THUD
            } else null

            return createDataForPrimitive(
                vibrator = vibrator,
                patternData = patternData,
                defaultType = defaultType,
                milliseconds = milliseconds ?: fallbackDuration,
                primitiveIdReference = primitiveIdReference,
                audioAttributes = audioAttributes
            )
        }
    },

    SPIN(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 40, 20, 40),
            amplitude = intArrayOf(255, 0, 255, 0)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            val primitiveIdReference = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                VibrationEffect.Composition::PRIMITIVE_SPIN
            } else null

            return createDataForPrimitive(
                vibrator = vibrator,
                defaultType = defaultType,
                patternData = patternData,
                milliseconds = milliseconds ?: fallbackDuration,
                primitiveIdReference = primitiveIdReference,
                audioAttributes = audioAttributes
            )
        }
    },

    QUICK_RISE(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 10, 30),
            amplitude = intArrayOf(255, 0, 255)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            val primitiveIdReference = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                VibrationEffect.Composition::PRIMITIVE_QUICK_RISE
            } else null

            return createDataForPrimitive(
                vibrator = vibrator,
                defaultType = defaultType,
                patternData = patternData,
                milliseconds = milliseconds ?: fallbackDuration,
                primitiveIdReference = primitiveIdReference,
                audioAttributes = audioAttributes
            )
        }
    },

    SLOW_RISE(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 100, 100),
            amplitude = intArrayOf(255, 0, 255)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            val primitiveIdReference = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                VibrationEffect.Composition::PRIMITIVE_SLOW_RISE
            } else null

            return createDataForPrimitive(
                vibrator = vibrator,
                defaultType = defaultType,
                patternData = patternData,
                milliseconds = milliseconds ?: fallbackDuration,
                primitiveIdReference = primitiveIdReference,
                audioAttributes = audioAttributes
            )
        }
    },

    QUICK_FALL(
        patternData = HapticPattern(
            pattern = longArrayOf(0, 30, 10),
            amplitude = intArrayOf(255, 0, 255)
        )
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData? {
            val primitiveIdReference = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                VibrationEffect.Composition::PRIMITIVE_QUICK_FALL
            } else null

            return createDataForPrimitive(
                vibrator = vibrator,
                defaultType = defaultType,
                patternData = patternData,
                milliseconds = milliseconds ?: fallbackDuration,
                primitiveIdReference = primitiveIdReference,
                audioAttributes = audioAttributes
            )
        }
    },

    /**
     * A custom [VibrationEffect] created with composition of many others [VibrationType] types,
     * such as [SLOW_RISE], [QUICK_FALL] and [TICK]
     *
     * ## References
     *
     * - [VibrationEffect.Composition](https://developer.android.com/reference/kotlin/android/os/VibrationEffect.Composition)
     */
    GROWS_INTENSITY_DIES_OFF {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            audioAttributes: AudioAttributes?
        ): HapticData {

            var effect: VibrationEffect? = null
            var combinedPattern: LongArray? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                /*
                 * Effect composition copied from:
                 * https://developer.android.com/reference/kotlin/android/os/VibrationEffect.Composition
                 */
                effect = VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE, 0.5f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_QUICK_FALL, 0.5f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 1.0f, 100)
                        .compose()
            } else if (SLOW_RISE.patternData != null
                        && QUICK_FALL.patternData != null
                        && TICK.patternData != null
            ) {
                /*
                 * TODO: [Improvement] The "pattern" and "amplitude" values still feels different from Composition
                 *       of API >= 30 above. I got these values from ChatGPT/OpenAI :(
                 */

                // Combine the patterns and amplitudes to create the custom composition pattern
                combinedPattern = longArrayOf(
                    *SLOW_RISE.patternData.pattern,
                    *QUICK_FALL.patternData.pattern,
                    *TICK.patternData.pattern
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                    vibrator?.hasAmplitudeControl() == true
                ) {

                    val combinedAmplitude = intArrayOf(
                        *SLOW_RISE.patternData.amplitude!!,
                        *QUICK_FALL.patternData.amplitude!!,
                        *TICK.patternData.amplitude!!
                    )

                    // Create the custom waveform vibration effect
                    effect = VibrationEffect.createWaveform(combinedPattern, combinedAmplitude, -1)
                }
            }

            return HapticData(
                effect = effect,
                patternData = if (combinedPattern != null) HapticPattern(pattern = combinedPattern) else null
            )
        }
    };

    companion object {

        var allDefaultType: VibrationType = SHORT

        @JvmStatic
        fun createDataForPrimitive(
            vibrator: Vibrator?,
            defaultType: VibrationType? = null,
            milliseconds: Long? = null,
            patternData: HapticPattern? = null,
            primitiveIdReference: KProperty0<Int>? = null,
            audioAttributes: AudioAttributes? = null
        ): HapticData? {

            if (!vibrator.isPrimitiveSupported(primitiveIdReference)) {
                return null
            }

            // Fallback to [defaultType] property if an ENUM value doesn't have
            // a definition to "milliseconds"
            val duration = if ((milliseconds == null || milliseconds <= 0) && defaultType != null) {
                defaultType.fallbackDuration
            } else milliseconds

            return HapticData(
                effectId = primitiveIdReference?.get(),
                fallbackDuration = duration,
                patternData = patternData,
                effectPrimitive = true,
                audioAttributes = audioAttributes
            )
        }

        @JvmStatic
        fun createDataForEffect(
            vibrator: Vibrator?,
            defaultType: VibrationType? = null,
            milliseconds: Long? = null,
            patternData: HapticPattern? = null,
            effectIdReference: KProperty0<Int>,
            audioAttributes: AudioAttributes? = null
        ): HapticData? {

            if (!vibrator.isEffectSupported(effectIdReference)) {
                return null
            }

            // Fallback to [defaultType] property if an ENUM value doesn't have
            // a definition to "milliseconds"
            val duration = if ((milliseconds == null || milliseconds <= 0) && defaultType != null) {
                defaultType.fallbackDuration
            } else milliseconds

            return HapticData(
                effectId = effectIdReference.get(),
                fallbackDuration = duration,
                patternData = patternData,
                audioAttributes = audioAttributes
            )
        }
    }
}
