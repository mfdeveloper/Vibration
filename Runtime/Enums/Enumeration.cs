using System;
using System.Linq;
using System.Reflection;
using System.Collections.Generic;

namespace VibrationPlugin.Enums
{
    /// <summary>
    /// Microsoft Enumeration class implementation with some improvements
    /// </summary>
    /// <remarks>
    /// <b> References </b>
    /// <ul>
    ///     <li>
    ///         <a href="https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/enumeration-classes-over-enum-types">
    ///         Use enumeration classes instead of enum types
    ///         </a>
    ///     </li>
    ///     <li>
    ///         <a href="https://josef.codes/enumeration-class-in-c-sharp-using-records">
    ///         Enumeration class in C# using records
    ///         </a>
    ///     </li>
    /// </ul>
    /// <br/>
    /// <b> PS: </b> Optionally, you can use <a href="https://josef.codes/enumeration-class-in-c-sharp-using-records/#record-implementation"> C# Record implementation </a>,
    ///  but keep in mind that is supported only on Unity <i> >= 2021.2</i>
    /// </remarks>
    /// <typeparam name="T"></typeparam>
    public abstract class Enumeration<T> : IComparable, IEquatable<T> where T : Enumeration<T>
    {
        protected static readonly Lazy<Dictionary<int, T>> AllValues;
        
        public string Name { get; }

        public int Value { get; }

        static Enumeration()
        {
            AllValues = new Lazy<Dictionary<int, T>>(FetchAll);
        }
        protected Enumeration(string name, int value) => (Name, Value) = (name, value);

        public override string ToString() => $"[${Name} => ${Value}]";

        public override bool Equals(object obj)
        {
            if (obj is not Enumeration<T> otherValue)
            {
                return false;
            }

            var typeMatches = GetType() == obj.GetType();
            var valueMatches = Value.Equals(otherValue.Value);

            return typeMatches && valueMatches;
        }

        public bool Equals(T other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            return Name == other.Name && Value == other.Value;
        }

        public override int GetHashCode()
        {
            return HashCode.Combine(Name, Value);
        }

        public int CompareTo(object other) => Value.CompareTo(((Enumeration<T>)other).Value);

        // Other utility methods ...
        
        protected static Dictionary<int, T> FetchAll() =>
            typeof(T).GetFields(BindingFlags.Public |
                                BindingFlags.Static |
                                BindingFlags.DeclaredOnly)
                .Select(fieldInfo => fieldInfo.GetValue(null))
                .Cast<T>()
                .ToDictionary(enumeration => enumeration.Value, enumeration => enumeration);
        
        public static IEnumerable<T> GetAll() => AllValues.Value.Values;
        
        public static bool operator ==(Enumeration<T> enumeration, Enumeration<T> other)
        {
            return enumeration?.Equals(other) == true;
        }

        public static bool operator !=(Enumeration<T> enumeration, Enumeration<T> other)
        {
            return enumeration?.Equals(other) == false;
        }

        public static bool operator ==(Enumeration<T> enumeration, string other)
        {
            return enumeration?.Name == other;
        }

        public static bool operator !=(Enumeration<T> enumeration, string other)
        {
            return enumeration?.Name != other;
        }
        
        public static bool operator ==(Enumeration<T> enumeration, int other)
        {
            return enumeration?.Value == other;
        }

        public static bool operator !=(Enumeration<T> enumeration, int other)
        {
            return enumeration?.Value != other;
        }
    }
}
