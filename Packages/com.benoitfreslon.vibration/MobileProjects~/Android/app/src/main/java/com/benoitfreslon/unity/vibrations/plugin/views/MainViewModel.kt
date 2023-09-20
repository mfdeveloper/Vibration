package com.benoitfreslon.unity.vibrations.plugin.views

import android.app.Application
import android.media.AudioAttributes
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import com.benoitfreslon.unity.vibrations.lib.Vibration
import com.benoitfreslon.unity.vibrations.lib.enums.VibrationType
import com.benoitfreslon.unity.vibrations.lib.extended.VibrationExtended
import java.util.concurrent.TimeUnit

open class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val vibration = Vibration(context = application.applicationContext)

    fun vibrateByType(vibrationType: VibrationType? = VibrationType.NORMAL) {
        vibration.vibrate(type = vibrationType)
    }

    fun vibrateByDuration() {
        vibration.vibrate(duration = 60, timeUnit = TimeUnit.MILLISECONDS)
    }

    fun vibrateByVibrationAttrs() {
        val vibrationExtended = VibrationExtended(context = getApplication<Application>().applicationContext)

        // Don't call "VibrationExtended" methods on devices with Android API < 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            vibrationExtended.vibrate(
                type = VibrationType.DOUBLE_TAP,
                audioAttributes = AudioAttributes.Builder().build()
            )
        }
    }
}
