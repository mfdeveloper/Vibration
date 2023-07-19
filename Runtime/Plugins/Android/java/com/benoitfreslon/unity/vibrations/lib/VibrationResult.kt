package com.benoitfreslon.unity.vibrations.lib

data class VibrationResult(var success: Boolean, var type: Type = Type.NONE) {

    enum class Type(val value: Int) {
        NONE(0),
        OK(1),
        EFFECT_NOT_SUPPORT(2),
        PATTERN_NOT_SUPPORT(3)
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
        return "AndroidPlugin: ${this::class.simpleName}(${::success.name}=$success, typeResult=${type.name})"
    }
}
