@file:JvmName("VibrationAttributesExt")

package com.benoitfreslon.unity.vibrations.lib.extensions

import android.annotation.TargetApi
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes

@get:TargetApi(Build.VERSION_CODES.R)
val VibrationAttributes?.isDefault: Boolean
    get() = this?.usage == VibrationAttributes.USAGE_UNKNOWN

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun VibrationAttributes?.toAudioAttrs(): AudioAttributes? {
    val audioAttrsBuilder = AudioAttributes.Builder()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        audioAttrsBuilder.predefinedAudioAttributes(this?.usage)
    } else audioAttrsBuilder.predefinedAudioAttributes()
}
