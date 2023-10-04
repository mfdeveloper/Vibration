package com.benoitfreslon.unity.vibrations.lib.entities

data class HapticPattern(
    var pattern: LongArray,
    var amplitude: IntArray? = null,
    var repeat: Int = -1
) {
    /**
     * Generated code by Android Studio
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HapticPattern

        if (!pattern.contentEquals(other.pattern)) return false
        if (repeat != other.repeat) return false

        return true
    }

    /**
     * Generated code by Android Studio
     */
    override fun hashCode(): Int {
        var result = pattern.contentHashCode()
        result = 31 * result + repeat
        return result
    }

    override fun toString(): String {
        return "${this::class.simpleName}(" +
                "${::pattern.name}=${pattern.contentToString()}, " +
                "${::amplitude.name}=${amplitude.contentToString()}, " +
                "${::repeat.name}=$repeat" +
        ")"
    }
}
