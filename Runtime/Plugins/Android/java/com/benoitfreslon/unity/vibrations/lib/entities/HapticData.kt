package com.benoitfreslon.unity.vibrations.lib.entities

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.benoitfreslon.unity.vibrations.lib.extensions.isDefault
import com.benoitfreslon.unity.vibrations.lib.extensions.predefinedAudioAttributes
import com.benoitfreslon.unity.vibrations.lib.extensions.functions.predefinedEffect
import com.benoitfreslon.unity.vibrations.lib.extensions.functions.predefinedPrimitive

open class HapticData(
    var effect: VibrationEffect? = null,
    open var fallbackDuration: Long? = null,
    var patternData: HapticPattern? = null,
    open var effectPrimitive: Boolean = false,
    audioAttributes: AudioAttributes? = null
) {
    var effectId: Int? = null
        protected set

    /**
     * In order to avoid warning: "_Kotlin calling non final function in constructor works_",
     * use [lazy] delegate instead of calling [initAudioAttributes] inside of a constructor.
     *
     * **See:** [Kotlin calling non final function in constructor](https://stackoverflow.com/a/50222496)
     */
    open val audioAttributes: AudioAttributes? by lazy {
        initAudioAttributes(audioAttributes)
    }

    val isEmpty: Boolean
        get() {
            return effect == null && fallbackDuration == null && patternData == null
        }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            effect = effect ?: predefinedEffect(id = VibrationEffect.EFFECT_TICK)
        }
    }

    @JvmOverloads
    constructor(
        effectId: Int?,
        fallbackDuration: Long? = null,
        patternData: HapticPattern? = null,
        effectPrimitive: Boolean = false,
        audioAttributes: AudioAttributes? = null
    ) : this(
        patternData = patternData,
        fallbackDuration = fallbackDuration,
        effectPrimitive = effectPrimitive,
        audioAttributes = audioAttributes
    ) {
        initEffectBy(effectId, effectPrimitive, fallbackDuration, patternData)
    }

    protected fun initEffectBy(
        effectId: Int?,
        effectPrimitive: Boolean,
        fallbackDuration: Long? = null,
        patternData: HapticPattern? = null
    ) {
        this.effectId = effectId

        effect = if (effectPrimitive) {
            predefinedPrimitive(vibrator, effectId, fallbackDuration, patternData)
        } else {
            effect ?: predefinedEffect(vibrator, effectId, fallbackDuration, patternData)
        }
    }

    protected open fun initAudioAttributes(audioAttributes: AudioAttributes? = null): AudioAttributes? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (audioAttributes != null && !audioAttributes.isDefault) audioAttributes else predefinedAudioAttributes()
        } else null
    }

    companion object {

        @JvmStatic
        var vibrator: Vibrator? = null
    }
}
