package com.benoitfreslon.unity.vibrations.lib.entities

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect

data class HapticData(
    var effect: VibrationEffect? = null,
    var attributes: AudioAttributes? = null,
    var vibrationAttributes: VibrationAttributes? = null
) {

    var effectId: Int? = null
        private set

    init {
        effect = effect ?: predefinedEffect()
        attributes = attributes ?: predefinedAudioAttributes()

        attributes?.let {
            vibrationAttributes = vibrationAttributes ?: predefinedVibrationAttributes(it)
        }
    }

    constructor(
        effectId: Int,
        effectPrimitive: Boolean = false,
        attributes: AudioAttributes? = null,
        vibrationAttributes: VibrationAttributes? = null
    ) : this(
        attributes = attributes,
        vibrationAttributes = vibrationAttributes
    ) {
        this.effectId = effectId

        effect = if (effectPrimitive) {
            predefinedPrimitive(effectId)
        } else {
            predefinedEffect(effectId)
        }
    }

    companion object {

        @JvmStatic
        fun predefinedEffect(id: Int? = null): VibrationEffect? {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                VibrationEffect.createPredefined(id ?: VibrationEffect.EFFECT_TICK)
            } else null
        }

        /**
         * Use [VibrationEffect.startComposition] to create a [VibrationEffect] to pass through
         * [android.os.VibratorManager.vibrate]
         *
         * ## References
         *
         * - [Android 12: VibratorManager & New Vibration Primitives](https://yggr.medium.com/exploring-android-12-vibratormanager-new-vibration-primitives-e862c95fe938)
         */
        @JvmStatic
        fun predefinedPrimitive(primitiveId: Int): VibrationEffect? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                VibrationEffect.startComposition()
                    .addPrimitive(primitiveId)
                    .compose()
            } else null
        }

        @JvmStatic
        fun predefinedAudioAttributes(audioUsage: Int? = null): AudioAttributes? {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                 AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(audioUsage ?: AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .build()
            } else null
        }

        @JvmStatic
        fun predefinedVibrationAttributes(it: AudioAttributes): VibrationAttributes? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                VibrationAttributes.Builder(it).build()
            } else null
        }
    }
}
