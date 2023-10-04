@file:JvmName("EnumExt")

package com.benoitfreslon.unity.vibrations.lib.extensions

/**
 * Create an enum from an Int.
 *
 * ## References
 * - [Create an enum from an Int in Kotlin?](https://stackoverflow.com/a/53524077)
 */
inline fun <reified T : Enum<T>> Enum.Companion.getByValue(value: Int? = null): T? {
    if (value == null || value == -1) {
        return null
    }

    val values = enumValues<T>()
    return values.firstOrNull {
        it.ordinal == value
    }
}

/**
 * Convert an enum from [TIn] to another one [TOut]
 */
inline fun <TIn : Enum<TIn>, reified TOut : Enum<TOut>> Enum<TIn>.toEnum(): TOut? {
    val values = enumValues<TOut>()

    return values.firstOrNull {
        it.ordinal == this.ordinal || it.name == this.name
    }
}

/**
 * Extension method alias for [toEnum]
 */
inline fun <TIn : Enum<TIn>, reified TOut : Enum<TOut>> Enum<TIn>.convertEnum() = toEnum<TIn, TOut>()
