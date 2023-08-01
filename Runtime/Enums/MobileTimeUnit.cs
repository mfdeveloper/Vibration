namespace VibrationPlugin.Enums
{
   public class MobileTimeUnit : Enumeration<MobileTimeUnit>
   {
      public static MobileTimeUnit Nanoseconds => new(0, nameof(Nanoseconds));
      public static MobileTimeUnit Microseconds => new(1, nameof(Microseconds));
      public static MobileTimeUnit Milliseconds => new(2, nameof(Milliseconds));
      public static MobileTimeUnit Seconds => new(3, nameof(Seconds));
      public static MobileTimeUnit Minutes => new(4, nameof(Minutes));
      public static MobileTimeUnit Hours => new(5, nameof(Hours));
      public static MobileTimeUnit Days => new(6, nameof(Days));

      public MobileTimeUnit(int id, string name)
         : base(id, name)
      {
      }
   }
}
