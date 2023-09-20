package com.benoitfreslon.unity.vibrations.lib

import com.benoitfreslon.unity.vibrations.lib.enums.VibrationType
import com.benoitfreslon.unity.vibrations.lib.extended.enums.VibrationTypeExtended
import com.benoitfreslon.unity.vibrations.lib.extensions.convertEnum
import com.benoitfreslon.unity.vibrations.lib.extensions.getByValue
import com.benoitfreslon.unity.vibrations.lib.extensions.toEnum
import com.google.common.truth.Truth.assertThat
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EnumExtensionsTest {

    @Test
    fun testGetAnEnumFromOrdinalValue() {
        val enum = Enum.getByValue<VibrationType>(VibrationType.SHORT.ordinal)

        assertThat(enum).isNotNull()
        assertThat(enum).isEqualTo(VibrationType.SHORT)
    }

    @Test
    fun testTryGetAnEnumFromInvalidOrdinal() {
        val enum = Enum.getByValue<VibrationType>(-2)

        assertThat(enum).isNull()
    }

    @Test
    fun testEnumConversion() {
        val convertedEnum = VibrationType.TICK?.convertEnum<VibrationType, VibrationTypeExtended>()
        // Check the method alias for ENUM conversion as well
        val anotherConvertedEnum = VibrationType.DOUBLE_TAP?.toEnum<VibrationType, VibrationTypeExtended>()

        assertThat(convertedEnum).isEqualTo(VibrationTypeExtended.TICK)
        assertThat(anotherConvertedEnum).isEqualTo(VibrationTypeExtended.DOUBLE_TAP)
    }
}
