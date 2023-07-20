package com.benoitfreslon.unity.vibrations.lib

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData
import com.benoitfreslon.unity.vibrations.lib.extensions.isEffectSupported
import com.benoitfreslon.unity.vibrations.lib.extensions.isPrimitiveSupported
import kotlin.reflect.KProperty0

enum class VibrationType(val value: Int) {
    SHORT(0) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ): HapticData? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createOneShot(
                        milliseconds ?: 0,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )

                HapticData(
                    effect = vibrationEffect,
                    vibrationAttributes = vibrationAttributes
                )
            } else {
                null
            }
        }
    },

    LIGHT(1) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForEffect(
            vibrator,
            VibrationEffect::EFFECT_TICK,
            vibrationAttributes
        )
    },

    TICK(2) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ): HapticData? {
           return LIGHT.getData(vibrator, milliseconds, vibrationAttributes)
        }
    },

    LOW_TICK(3) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForPrimitive(
            vibrator,
            VibrationEffect.Composition::PRIMITIVE_LOW_TICK,
            vibrationAttributes
        )
    },

    MEDIUM(4) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForEffect(
            vibrator,
            VibrationEffect::EFFECT_CLICK,
            vibrationAttributes
        )
    },

    DOUBLE_TAP(5) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForEffect(
            vibrator,
            VibrationEffect::EFFECT_DOUBLE_CLICK,
            vibrationAttributes
        )
    },

    DOUBLE_CLICK(6) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ): HapticData? {
            return DOUBLE_TAP.getData(vibrator, milliseconds, vibrationAttributes)
        }
    },

    HEAVY(7) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForEffect(
            vibrator,
            VibrationEffect::EFFECT_HEAVY_CLICK,
            vibrationAttributes
        )
    },

    SPIN(7) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForPrimitive(
            vibrator,
            VibrationEffect.Composition::PRIMITIVE_SPIN,
            vibrationAttributes
        )
    },

    THUD(8) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataForPrimitive(
            vibrator,
            VibrationEffect.Composition::PRIMITIVE_THUD,
            vibrationAttributes
        )
    };

    open fun getData(vibrator: Vibrator? = null, milliseconds: Long? = null, attributes: AudioAttributes): HapticData? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val vibrationAttributes = VibrationAttributes.Builder(attributes).build()
            getData(vibrator, milliseconds, vibrationAttributes)
        } else null
    }
    abstract fun getData(vibrator: Vibrator? = null, milliseconds: Long? = null, vibrationAttributes: VibrationAttributes? = null): HapticData?

    companion object {

        private val values = values()

        /**
         * Create an enum from an Int.
         *
         * ## References
         * - [Create an enum from an Int in Kotlin?](https://stackoverflow.com/a/53524077)
         */
        @JvmStatic
        fun getByValue(value: Int) = values.firstOrNull { it.value == value }

        @JvmStatic
        fun createDataForPrimitive(
            vibrator: Vibrator?,
            primitiveIdReference: KProperty0<Int>,
            vibrationAttributes: VibrationAttributes?
        ): HapticData? {

            if (!vibrator.isPrimitiveSupported(primitiveIdReference)) {
                return null
            }

            return HapticData(
                effectId = primitiveIdReference.get(),
                effectPrimitive = true,
                vibrationAttributes = vibrationAttributes
            )
        }

        @JvmStatic
        fun createDataForEffect(
            vibrator: Vibrator?,
            effectIdReference: KProperty0<Int>,
            vibrationAttributes: VibrationAttributes?
        ): HapticData? {

            if (!vibrator.isEffectSupported(effectIdReference)) {
                return null
            }

            return HapticData(
                effectId = effectIdReference.get(),
                vibrationAttributes = vibrationAttributes
            )
        }
    }
}
