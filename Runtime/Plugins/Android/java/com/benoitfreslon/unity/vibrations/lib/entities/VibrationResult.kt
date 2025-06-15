package com.benoitfreslon.unity.vibrations.lib.entities

import com.benoitfreslon.unity.vibrations.lib.enums.VibrationType
import com.benoitfreslon.unity.vibrations.lib.extended.enums.VibrationTypeExtended

data class VibrationResult @JvmOverloads constructor(
    var success: Boolean = false,
    var type: Type = Type.NONE,
    var duration: Long? = null,
    var patternData: HapticPattern? = null,
    var vibrationType: VibrationType? = null,
    var vibrationTypeExtended: VibrationTypeExtended? = null
) {

    enum class Type(val value: Int) {
        NONE(0),
        OK(1),
        ERROR(2),
        EFFECT_NOT_SUPPORT(3),
        PATTERN_NOT_SUPPORT(4),
        VIBRATOR_NOT_SUPPORT(5),
        DURATION_OR_TYPE_REQUIRED(6),
        ATTRIBUTES_MISSING(7)
    }

    val typeName get() = type.name

    fun compareType(value: Int): Boolean = value == type.value

    fun equals(other: VibrationResult): Boolean {
        return super.equals(other) || compareType(other.type.value)
    }

    override fun equals(other: Any?): Boolean {

        if (other is VibrationResult) {
            return equals(other)
        }

        return super.equals(other)
    }

    /**
     * [Any.hashCode] generated implementation on Android Studio,
     * when implement [Any.equals]
     */
    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        val defaultResult = "VibrationPlugin: ${this::class.simpleName}(${::success.name}=$success, typeResult=$typeName"
        val builder = StringBuilder(defaultResult)

        if (vibrationType != null) {
            builder.append(", vibrationType=${vibrationType?.name}")
                .append(", vibrationTypeValue=${vibrationType?.ordinal}")
        }

        if (duration != null) {
            builder.append(", durationMilliseconds=$duration")
        }

        if (patternData != null) {
            builder.append(", pattern=$patternData")
        }

        builder.append(")")

        return builder.toString()
    }
}
