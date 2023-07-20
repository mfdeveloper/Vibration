@file:JvmName("VibratorExt")

package com.benoitfreslon.unity.vibrations.lib.extensions

import android.os.Build
import android.os.Vibrator
import android.util.Log
import com.benoitfreslon.unity.vibrations.lib.Vibration
import kotlin.reflect.KProperty0

fun Vibrator?.isEffectSupported(effectId: Int): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this?.areAllEffectsSupported(effectId) == Vibrator.VIBRATION_EFFECT_SUPPORT_YES
    } else false
}

fun Vibrator?.isEffectSupported(effectIdReference: KProperty0<Int>): Boolean {

    val isSupported = isEffectSupported(effectIdReference.get())
    if (!isSupported) {
        Log.w(
            Vibration.TAG,
            "The effect: '${effectIdReference.name}' isn't supported!"
        )
    }

    return isSupported
}

fun Vibrator?.isPrimitiveSupported(primitiveId: Int): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        this?.areAllPrimitivesSupported(primitiveId) == true
    } else false
}

fun Vibrator?.isPrimitiveSupported(primitiveIdReference: KProperty0<Int>): Boolean {

    val isSupported = isPrimitiveSupported(primitiveIdReference.get())
    if (!isSupported) {
        Log.w(
            Vibration.TAG,
            "The PRIMITIVE effect: '${primitiveIdReference.name}' isn't supported!"
        )
    }

    return isSupported
}
