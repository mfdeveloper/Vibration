package com.benoitfreslon.unity.vibrations.lib.extended.enums

import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
import android.os.Vibrator
import com.benoitfreslon.unity.vibrations.lib.enums.VibrationType
import com.benoitfreslon.unity.vibrations.lib.entities.HapticData
import com.benoitfreslon.unity.vibrations.lib.entities.HapticPattern
import com.benoitfreslon.unity.vibrations.lib.enums.IVibrationType
import com.benoitfreslon.unity.vibrations.lib.extended.entities.HapticDataExtended

enum class VibrationTypeExtended(
    val fallbackDuration: Long? = null,
    val patternData: HapticPattern? = null,
    var defaultType: VibrationType? = VibrationType.allDefaultType
) : IVibrationType {

    SHORT(
        fallbackDuration = VibrationType.SHORT.fallbackDuration,
        patternData = VibrationType.SHORT.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.SHORT,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    LIGHT(
        fallbackDuration = VibrationType.LIGHT.fallbackDuration,
        patternData = VibrationType.LIGHT.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.LIGHT,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    TICK(
        fallbackDuration = VibrationType.TICK.fallbackDuration,
        patternData = VibrationType.TICK.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.TICK,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    LOW_TICK(
        fallbackDuration = VibrationType.LOW_TICK.fallbackDuration,
        patternData = VibrationType.LOW_TICK.patternData
    )  {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.LOW_TICK,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    NORMAL(
        fallbackDuration = VibrationType.NORMAL.fallbackDuration,
        patternData = VibrationType.NORMAL.patternData
    ){

        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.NORMAL,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    CLICK(
        fallbackDuration = VibrationType.CLICK.fallbackDuration,
        patternData = VibrationType.CLICK.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.CLICK,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    DOUBLE_TAP(
        fallbackDuration = VibrationType.DOUBLE_TAP.fallbackDuration,
        patternData = VibrationType.DOUBLE_TAP.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.DOUBLE_TAP,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    DOUBLE_CLICK(
        fallbackDuration = VibrationType.DOUBLE_CLICK.fallbackDuration,
        patternData = VibrationType.DOUBLE_CLICK.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ): HapticDataExtended? {

            return DOUBLE_TAP.getData(
                vibrator,
                milliseconds,
                vibrationAttributes
            )
        }
    },

    HEAVY(
        fallbackDuration = VibrationType.HEAVY.fallbackDuration,
        patternData = VibrationType.HEAVY.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.HEAVY,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    THUD(
        fallbackDuration = VibrationType.THUD.fallbackDuration,
        patternData = VibrationType.THUD.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.THUD,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    SPIN(
        fallbackDuration = VibrationType.SPIN.fallbackDuration,
        patternData = VibrationType.SPIN.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.SPIN,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    QUICK_RISE(
        fallbackDuration = VibrationType.QUICK_RISE.fallbackDuration,
        patternData = VibrationType.QUICK_RISE.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.QUICK_RISE,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    SLOW_RISE(
        fallbackDuration = VibrationType.SLOW_RISE.fallbackDuration,
        patternData = VibrationType.SLOW_RISE.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.SLOW_RISE,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    QUICK_FALL(
        fallbackDuration = VibrationType.QUICK_FALL.fallbackDuration,
        patternData = VibrationType.QUICK_FALL.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.QUICK_FALL,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    },

    GROWS_INTENSITY_DIES_OFF(
        fallbackDuration = VibrationType.GROWS_INTENSITY_DIES_OFF.fallbackDuration,
        patternData = VibrationType.GROWS_INTENSITY_DIES_OFF.patternData
    ) {
        override fun getData(
            vibrator: Vibrator?,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes?
        ) = createDataWithVibrationAttrs(
            vibrator,
            VibrationType.GROWS_INTENSITY_DIES_OFF,
            milliseconds ?: fallbackDuration,
            vibrationAttributes
        )
    };

    @Suppress("unused")
    override fun getData(vibrator: Vibrator?, milliseconds: Long?, audioAttributes: AudioAttributes?): HapticData? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val vibrationAttributes = if (audioAttributes != null) {
                VibrationAttributes.Builder(audioAttributes).build()
            } else null

            getData(vibrator, milliseconds, vibrationAttributes)
        } else null
    }

    abstract fun getData(vibrator: Vibrator? = null, milliseconds: Long? = null, vibrationAttributes: VibrationAttributes? = null): HapticDataExtended?

    companion object {

        @JvmStatic
        @JvmOverloads
        fun createDataWithVibrationAttrs(
            vibrator: Vibrator?,
            vibrationType: VibrationType,
            milliseconds: Long?,
            vibrationAttributes: VibrationAttributes? = null
        ): HapticDataExtended {

            val hapticData = vibrationType.getData(
                vibrator,
                milliseconds ?: vibrationType.fallbackDuration
            )

            return HapticDataExtended(hapticData).apply {
                this.vibrationAttributes = vibrationAttributes
            }
        }
    }
}
