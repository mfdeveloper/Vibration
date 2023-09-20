@file:JvmName("AudioAttributesExt")

package com.benoitfreslon.unity.vibrations.lib.extensions

import android.annotation.TargetApi
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes

@get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
val AudioAttributes?.isDefault: Boolean
    get() {
        return this?.usage == AudioAttributes.USAGE_UNKNOWN && this.contentType == AudioAttributes.CONTENT_TYPE_UNKNOWN
    }

@Suppress("UnusedReceiverParameter")
@get:TargetApi(Build.VERSION_CODES.LOLLIPOP)
val AudioAttributes.Builder.predefinedValues: MutableMap<String, Int>
    get() = mutableMapOf(
        "content" to AudioAttributes.CONTENT_TYPE_SONIFICATION,
        "usage" to AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
    )
fun AudioAttributes.Builder.predefinedAudioAttributes(audioUsage: Int? = null): AudioAttributes? {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.setContentType(this.predefinedValues["content"] ?: AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(audioUsage ?: (this.predefinedValues["usage"] ?: AudioAttributes.USAGE_ASSISTANCE_SONIFICATION))
            .build()
    } else null
}

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.R)
fun AudioAttributes?.toVibrationAttrs(): VibrationAttributes {

     if (this != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
       return VibrationAttributes.createForUsage(this.usage)
    }

    val vibrationAttrBuilder = VibrationAttributes.Builder()

    when (this?.usage) {
        AudioAttributes.USAGE_NOTIFICATION,
        AudioAttributes.USAGE_NOTIFICATION_EVENT,
        AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED,
        AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT,
        AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST -> {
            vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_NOTIFICATION)
        }
        AudioAttributes.USAGE_VOICE_COMMUNICATION,
        AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING,
        AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE,
        AudioAttributes.USAGE_ASSISTANT -> {
            vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_COMMUNICATION_REQUEST)
        }

        AudioAttributes.USAGE_NOTIFICATION_RINGTONE -> vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_RINGTONE)
        AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_ACCESSIBILITY)
        }

        AudioAttributes.USAGE_ASSISTANCE_SONIFICATION -> vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_TOUCH)
        AudioAttributes.USAGE_ALARM -> vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_ALARM)
        AudioAttributes.USAGE_MEDIA,
        AudioAttributes.USAGE_GAME -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_MEDIA)
        }

        else -> vibrationAttrBuilder.setUsage(VibrationAttributes.USAGE_UNKNOWN)
    }

    return vibrationAttrBuilder.build()
}

// -- Functions --

@JvmOverloads
fun predefinedAudioAttributes(audioUsage: Int? = null, audioAttributesBuilder: AudioAttributes.Builder? = null): AudioAttributes? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        (audioAttributesBuilder ?: AudioAttributes.Builder()).predefinedAudioAttributes(audioUsage)
    } else null
}
