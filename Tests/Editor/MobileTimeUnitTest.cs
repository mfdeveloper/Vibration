using System;
using NUnit.Framework;
using VibrationPlugin.Enums;

namespace VibrationPlugin.EditorTests
{
    public class MobileTimeUnitTest
    {

        [Test, Description("Test if a time unit defined as Milliseconds returns a duration in milliseconds")]
        public void TestToMillisecondsReturnsCorrectValueForMilliseconds()
        {
            // Arrange
            MobileTimeUnit timeUnit = MobileTimeUnit.Milliseconds;
            long duration = 1000;

            // Act
            int milliseconds = timeUnit.ToMilliseconds(duration);

            // Assert
            Assert.AreEqual(1000, milliseconds);
        }

        [Test, Description("Test if a time unit defined as Seconds returns a duration in milliseconds")]
        public void TestToMillisecondsReturnsCorrectValueForSeconds()
        {
            // Arrange
            MobileTimeUnit timeUnit = MobileTimeUnit.Seconds;
            long duration = 1;

            // Act
            int milliseconds = timeUnit.ToMilliseconds(duration);

            // Assert
            Assert.AreEqual(1000, milliseconds);
        }

        [Test, Description("Test if a time unit defined as Minutes returns a duration in milliseconds")]
        public void TestToMillisecondsReturnsCorrectValueForMinutes()
        {
            // Arrange
            MobileTimeUnit timeUnit = MobileTimeUnit.Minutes;
            long duration = 1;

            // Act
            int milliseconds = timeUnit.ToMilliseconds(duration);

            // Assert
            Assert.AreEqual(60000, milliseconds);
        }

        [Test, Description("Test if a time unit defined as Hours returns a duration in milliseconds")]
        public void TestToMillisecondsReturnsCorrectValueForHours()
        {
            // Arrange
            MobileTimeUnit timeUnit = MobileTimeUnit.Hours;
            long duration = 1;

            // Act
            int milliseconds = timeUnit.ToMilliseconds(duration);

            // Assert
            Assert.AreEqual(3600000, milliseconds);
        }

        [Test, Description("Test if a time unit defined as Days returns a duration in milliseconds")]
        public void TestToMillisecondsReturnsCorrectValueForDays()
        {
            // Arrange
            MobileTimeUnit timeUnit = MobileTimeUnit.Days;
            long duration = 1;

            // Act
            int milliseconds = timeUnit.ToMilliseconds(duration);

            // Assert
            Assert.AreEqual(86400000, milliseconds);
        }

        [Test, Description("Test if an unsupported time unit throws an exception when try convert to milliseconds")]
        public void TestToMillisecondsThrowsArgumentExceptionForUnsupportedTimeUnit()
        {
            // Arrange
            MobileTimeUnit timeUnit = MobileTimeUnit.Nanoseconds;
            long duration = 1;

            // Act
            Assert.Throws<ArgumentException>(() => timeUnit.ToMilliseconds(duration));
        }
    }
}
