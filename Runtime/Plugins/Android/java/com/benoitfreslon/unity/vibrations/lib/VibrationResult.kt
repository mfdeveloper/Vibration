package com.benoitfreslon.unity.vibrations.lib

data class VibrationResult(var success: Boolean, var type: Type = Type.NONE) {

    enum class Type(val value: Int) {
        NONE(0),
        OK(1),
        EFFECT_NOT_SUPPORT(2),
        PATTERN_NOT_SUPPORT(3)
    }

    fun typeName(): String {
        return type.name
    }

    fun compareType(value: Int): Boolean = value == type.value
}