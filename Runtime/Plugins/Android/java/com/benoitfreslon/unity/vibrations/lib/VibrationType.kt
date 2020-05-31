package com.benoitfreslon.unity.vibrations.lib

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect

/**
 * TODO: Use vibrator.areEffectsSupported([effectIds]) to check if
 *       can use EFFECT_CLICK, EFFECT_TICK...
 */
enum class VibrationType(val value: Int) {
    SHORT(0) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var vibrationEffect =
                    VibrationEffect.createOneShot(
                        milliseconds!!,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )

                HapticData(
                    effect = vibrationEffect,
                    attributes = attributes
                )
            } else {
                null
            }
        }
    },

    LIGHT(1) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                HapticData(effectId = VibrationEffect.EFFECT_TICK)
            } else {
                null
            }
        }
    },

    TICK(2) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? = (LIGHT::getData)(milliseconds, attributes)
    },

    MEDIUM(3) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                 HapticData(effectId = VibrationEffect.EFFECT_CLICK)
            } else {
                null
            }
        }
    },

    DOUBLE_TAP(4) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                HapticData(effectId = VibrationEffect.EFFECT_DOUBLE_CLICK)
            } else {
                null
            }
        }
    },

    DOUBLE_CLICK(5) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? = (DOUBLE_TAP::getData)(milliseconds, attributes)
    },

    HEAVY(6) {
        override fun getData(milliseconds: Long?, attributes: AudioAttributes?): HapticData? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return HapticData(effectId = VibrationEffect.EFFECT_HEAVY_CLICK)
            } else {
                null
            }
        }
    };

    abstract fun getData(milliseconds: Long? = null, attributes: AudioAttributes? = null): HapticData?

    /** Create an enum from a Int
     * @see https://stackoverflow.com/questions/53523948/how-do-i-create-an-enum-from-a-int-in-kotlin/53524077
     */
    companion object {
        private val values = values()

        @JvmStatic
        fun getByValue(value: Int) = values.firstOrNull { it.value == value }
    }
}