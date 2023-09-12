namespace VibrationPlugin.Enums
{
   /// <summary>
   /// A string <i>Enum</i> using custom <b>Enumeration class pattern</b>
   /// </summary>
   /// <remarks>
   /// <b> References </b>
   /// <ul>
   ///   <li>
   ///      <a href="https://josipmisko.com/posts/string-enums-in-c-sharp-everything-you-need-to-know">String Enums in C#: Everything You Need to Know</a>
   ///   </li>
   /// </ul>
   /// </remarks>
   public class MobileTimeUnit : Enumeration<MobileTimeUnit>
   {
      public static MobileTimeUnit Nanoseconds => new(nameof(Nanoseconds), 0 );
      public static MobileTimeUnit Microseconds => new(nameof(Nanoseconds), 1);
      public static MobileTimeUnit Milliseconds => new(nameof(Milliseconds), 2);
      public static MobileTimeUnit Seconds => new(nameof(Seconds), 3 );
      public static MobileTimeUnit Minutes => new(nameof(Minutes), 4);
      public static MobileTimeUnit Hours => new(nameof(Hours), 5 );
      public static MobileTimeUnit Days => new(nameof(Days), 6 );

      public MobileTimeUnit(string name, int value)
         : base(name, value)
      {
      }
   }
}
