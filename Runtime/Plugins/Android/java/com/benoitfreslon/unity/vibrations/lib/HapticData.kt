package com.benoitfreslon.unity.vibrations.lib

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect

data class HapticData(var attributes: AudioAttributes? = null, var effect: VibrationEffect? = null) {

    private lateinit var attributesBuilder: AudioAttributes.Builder

    init {
        effect = effect ?: predefinedEffect()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            attributesBuilder = AudioAttributes.Builder()
            attributes = attributes ?: attributesBuilder
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build()
        }
    }

    constructor(effectId: Int, attributes: AudioAttributes? = null) : this(attributes) {
        effect = predefinedEffect(effectId)
    }

    companion object {

        @JvmStatic
        fun predefinedEffect(id: Int? = null): VibrationEffect? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                VibrationEffect.createPredefined(id ?: VibrationEffect.EFFECT_TICK)
            } else {
                return null
            }
        }
    }
}