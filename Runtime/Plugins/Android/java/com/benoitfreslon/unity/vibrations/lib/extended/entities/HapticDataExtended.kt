package com.benoitfreslon.unity.vibrations.lib.extended.entities

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData
import com.benoitfreslon.unity.vibrations.lib.entities.HapticPattern
import com.benoitfreslon.unity.vibrations.lib.extensions.isDefault
import com.benoitfreslon.unity.vibrations.lib.extensions.functions.predefinedEffect
import com.benoitfreslon.unity.vibrations.lib.extensions.toVibrationAttrs

class HapticDataExtended(
    effect: VibrationEffect? = predefinedEffect(),
    fallbackDuration: Long? = null,
    patternData: HapticPattern? = null,
    effectPrimitive: Boolean = false,
    audioAttributes: AudioAttributes? = null,
    var vibrationAttributes: VibrationAttributes? = null
) : HapticData(
    effect,
    fallbackDuration,
    patternData,
    effectPrimitive,
    audioAttributes
) {

    @JvmOverloads
    constructor(
        effectId: Int?,
        fallbackDuration: Long? = null,
        patternData: HapticPattern? = null,
        effectPrimitive: Boolean = false,
        attributes: AudioAttributes? = null,
        vibrationAttributes: VibrationAttributes? = null
    ) : this(
        fallbackDuration = fallbackDuration,
        patternData = patternData,
        effectPrimitive = effectPrimitive,
        audioAttributes = attributes,
        vibrationAttributes = vibrationAttributes
    ) {
        initEffectBy(effectId, effectPrimitive, fallbackDuration)
    }

    constructor(hapticData: HapticData?) : this(
        effectId = hapticData?.effectId,
        fallbackDuration = hapticData?.fallbackDuration,
        effectPrimitive = hapticData?.effectPrimitive ?: false,
        patternData = hapticData?.patternData,
        attributes = hapticData?.audioAttributes
    )

    override fun initAudioAttributes(audioAttributes: AudioAttributes?): AudioAttributes? {
        val attributes = super.initAudioAttributes(audioAttributes)

        attributes?.let {
            vibrationAttributes = if (!vibrationAttributes.isDefault) vibrationAttributes else predefinedVibrationAttributes(it)
        }

        return attributes
    }

    companion object {

        @JvmStatic
        fun predefinedVibrationAttributes(audioAttributes: AudioAttributes): VibrationAttributes? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                audioAttributes.toVibrationAttrs()
            } else null
        }
    }
}
