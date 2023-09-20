package com.benoitfreslon.unity.vibrations.lib.enums

import android.media.AudioAttributes
import android.os.Vibrator
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData

interface IVibrationType {
    fun getData(vibrator: Vibrator? = null, milliseconds: Long? = null, audioAttributes: AudioAttributes? = null): HapticData?
}
