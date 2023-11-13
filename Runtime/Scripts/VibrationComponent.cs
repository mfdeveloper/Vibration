using System;
using System.Diagnostics.CodeAnalysis;
using UnityEngine;
using VibrationPlugin.Save;
using VibrationPlugin.Enums;

namespace VibrationPlugin
{
    [AddComponentMenu("VibrationPlugin/Vibration")]
    public class VibrationComponent : MonoBehaviour
    {
        private const string TAG = nameof(VibrationComponent);
        
        #region Fields

        [SerializeField]
        [Tooltip("Enable/Disable vibration for all in the game")]
        protected bool enable = true;

        [Header("Save and Load")]
        [SerializeField]
        [Tooltip("ScriptableObject with attributes when enable/disable vibration config (use PlayerPrefs to save from a Menu, for example)")]
        protected Options options;

        [Header("Effect")]
        [SerializeField]
        [Tooltip("Vibration duration in seconds")]
        protected long duration = 2;

        [SerializeField] 
        [Tooltip("The type of vibration effect (SHORT: Android <= 9, LIGHT...HEAVY: Android >= 10)")]
        protected VibrationType type = VibrationType.None;
        
        // -- Unity Android core instances --
        protected AndroidJavaClass unityPlayer;
        protected AndroidJavaObject currentActivity;
        
        // -- VibrationPlugin required instances --
        protected AndroidJavaObject vibrationPlugin;
        protected AndroidJavaObject durationLongObj;

        #endregion

        #region Properties

        public virtual bool IsEnabled
        {
            get
            {
                bool isEnable = enable;

                if (options != null)
                {
                    isEnable = enable && options.vibration;
                }

                return isEnable;
            }
        }

        public long Duration => duration;
        public VibrationType Type => type;

        #endregion

        #region Unity Events

        protected virtual void Awake()
        {
            LoadOptionsSaved();
        }
        //
        /// <summary>
        /// Start is called before the first frame update
        /// </summary>
        /// <remarks>
        /// <b> References </b>
        /// <br/><br/>
        /// <ul>
        ///     <li> 
        ///        - <a href="https://stackoverflow.com/a/67058986">Field currentActivity or type signature not found</a> 
        ///     </li>
        /// </ul>
        /// </remarks>
        [SuppressMessage("ReSharper", "StringLiteralTypo")]
        protected virtual void Start()
        {
            InitComponents();
        }

        protected virtual void OnDisable()
        {
            #if UNITY_ANDROID

            if (unityPlayer != null)
            {
                unityPlayer.Dispose();
                unityPlayer = null;
            }

            if (currentActivity != null)
            {
                currentActivity.Dispose();
                currentActivity = null;
            }

            if (vibrationPlugin != null)
            {
                vibrationPlugin.Dispose();
                vibrationPlugin = null;
            }

            if (durationLongObj != null)
            {
                durationLongObj.Dispose();
                durationLongObj = null;
            }

            #endif
        }

        #endregion

        [SuppressMessage("ReSharper", "StringLiteralTypo")]
        protected virtual void InitComponents()
        {
            #if UNITY_ANDROID
                        
            // Avoid: "Field currentActivity or type signature not found" exception
            // when a device is connected over USB and using Unity Remote app
            try
            {    
                // PS: If you wish instantiate Kotlin Companion or Java static classes straight-forward from full package name,
                // use the "$" special character (e.g "android.media.AudioAttributes$Builder")
                unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
                currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
                vibrationPlugin = new AndroidJavaObject("com.benoitfreslon.unity.vibrations.lib.Vibration", currentActivity);
            }
            catch (Exception err)
            {
                if (!err.Message.Contains("Field currentActivity"))
                {
                    throw;
                }

                if (Debug.isDebugBuild)
                {
                    var warnMessage = $"[{TAG}] [Android] Error on load fields/components." +
                                            $"Exception => {err.GetType().Name} : {err.Message}";
                    
                    Debug.LogWarning(warnMessage);   
                }
            }

            #endif
        }
        
        protected virtual void LoadOptionsSaved()
        {
            if (options == null)
            { 
                /*
                 * Fallback to some Unity versions that ScriptableObjects
                 * passed by inspector disappears when plays.
                 * 
                 * Or, if a developer forget to define a custom ScriptableObject
                 * to save options on inspector
                 */ 
                options = ScriptableObject.CreateInstance<Options>();

                if (Debug.isDebugBuild)
                {
                    Debug.LogWarningFormat("[{0}] The parameter '{1}' is required to be defined in inspector", TAG, nameof(options));
                }
            }

            if (!PlayerPrefs.HasKey(options.GetType().Name))
            {
                return;
            }
            
            var jsonOptions = PlayerPrefs.GetString(options.GetType().Name);

            // Not use JsonUtility.FromJson() to ScriptableObject or MonoBehaviour objects
            JsonUtility.FromJsonOverwrite(jsonOptions, options);
        }
        
        /// <summary>
        /// Check if the <paramref name="milliseconds"/> and a <see cref="VibrationType"/>
        /// match with "default" values.
        /// </summary>
        /// <param name="milliseconds">Duration of vibration in milliseconds</param>
        /// <param name="vibrationType"><see cref="VibrationType"/> to be verified the default value</param>
        /// <returns></returns>
        [SuppressMessage("ReSharper", "MemberCanBeMadeStatic.Global")]
        [SuppressMessage("ReSharper", "MemberCanBePrivate.Global")]
        public bool IsDefault(long? milliseconds = null, VibrationType vibrationType = VibrationType.None)
        {
            return vibrationType == VibrationType.HandheldDefault ||
                   (milliseconds is null or 0 && vibrationType == VibrationType.None);
        }

        ///<summary>
        /// Tiny pop vibration
        ///</summary>
        public virtual void VibratePop()
        {
            #if UNITY_IOS && !UNITY_EDITOR

            Vibration.VibratePop();

            #elif UNITY_ANDROID && !UNITY_EDITOR

            Vibrate(15);

            #endif
        }

        ///<summary>
        /// Small peek vibration
        ///</summary>
        public virtual void VibratePeek()
        {
            #if UNITY_IOS && !UNITY_EDITOR

            Vibration.VibratePeek();

            #elif UNITY_ANDROID && !UNITY_EDITOR

            Vibrate(25);

            #endif
        }

        ///<summary>
        /// 3 small vibrations
        ///</summary>
        public virtual void VibrateNope()
        {
            #if UNITY_IOS && !UNITY_EDITOR

            Vibration.VibrateNope();

            #elif UNITY_ANDROID && !UNITY_EDITOR

		    long [] pattern = { 0, 5, 5, 5 };
		    Vibrate(pattern, VibrationRepeat.NoRepeat);

            #endif
        }

        public virtual void Vibrate(long? milliseconds = null, VibrationType vibrationType = VibrationType.None)
        {
            if (!IsEnabled)
            {
                return;
            }

            if (milliseconds == null)
            {
                milliseconds = duration;
            }

            if (vibrationType == VibrationType.None)
            {
                vibrationType = type;
            }

            if (IsDefault(milliseconds, vibrationType))
            {
                #if !UNITY_WEBGL
                
                Vibration.Vibrate();
                return;
                
                #endif
            }

            #if UNITY_ANDROID
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }
            
            durationLongObj = new AndroidJavaObject("java.lang.Long", milliseconds);
            var result = vibrationPlugin.Call<AndroidJavaObject>("vibrate", durationLongObj, (int) vibrationType);

            if (result != null && Debug.isDebugBuild)
            {
                // TODO: Add a UI text in the scene, with the log below when generate a Development Build
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                Debug.Log($"[{TAG}] EFFECT: ${vibrationType}, RESULT: ${result.Call<string>("toString")}");
            }
            
            #elif UNITY_IOS && !UNITY_EDITOR
            
            Vibration.Vibrate();
            
            #elif UNITY_WEBGL

            int intMilliseconds = Convert.ToInt32(milliseconds);
            Vibration.Vibrate(intMilliseconds, (int) vibrationType);

            #endif
        }
        
        public virtual void Vibrate(long duration, MobileTimeUnit timeUnit)
        {
            
            if (!IsEnabled)
            {
                return;
            }
            
            #if UNITY_ANDROID
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }
                
            durationLongObj = new AndroidJavaObject("java.lang.Long", duration);
            AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibrateWithTimeUnit", durationLongObj, timeUnit.ToString());
            
            if (result != null && Debug.isDebugBuild)
            {
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                Debug.Log($"[{TAG}] RESULT: ${result.Call<string>("toString")}, ${nameof(timeUnit)}: ${timeUnit}");
            }

            #elif UNITY_IOS || UNITY_WEBGL

            // Convert to milliseconds from "MobileTimeUnit" defined in "timeUnit" parameter
            if (timeUnit != MobileTimeUnit.Milliseconds)
            {
                duration = timeUnit.ToMilliseconds(duration);
            }

            Vibration.Vibrate(duration);

            #endif
        }

        ///<summary>
        /// Only on Android
        ///</summary>
        /// <remarks>
        /// <b> References </b>
        /// <ul>
        ///     <li>
        ///         <a href="https://proandroiddev.com/using-vibrate-in-android-b0e3ef5d5e07"> Using Vibrate In Android </a>
        ///     </li>
        /// </ul>
        /// </remarks>
        public virtual void Vibrate(long[] pattern, VibrationRepeat repeat = VibrationRepeat.NoRepeat)
        {
            if (!IsEnabled)
            {
                return;
            }
            
            #if UNITY_ANDROID
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }

            AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibrate", pattern, (int) repeat);
        
            if (result != null && Debug.isDebugBuild)
            {
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                Debug.Log($"[{TAG}] RESULT: ${result.Call<string>("toString")}, ${nameof(pattern)}: ${pattern}, ${nameof(repeat)}: ${repeat}");
            }

            #elif UNITY_WEBGL

            var intPattern = Array.ConvertAll(pattern, Convert.ToInt32);
            Vibration.VibrateWithPattern(intPattern, intPattern.Length, (int) type);

            #elif UNITY_IOS && !UNITY_EDITOR
            
            Vibration.Vibrate();
            
            #endif
        }

        public virtual bool HasVibrator()
        {
            #if UNITY_ANDROID
            
            if (vibrationPlugin == null)
            {
                return false;
            }

            var hasVibrator = vibrationPlugin.Call<bool>("hasVibrator");
            return hasVibrator;

            #elif UNITY_IOS || UNITY_WEBGL

            return Vibration.HasVibrator();

            #else
            
            return false;
            
            #endif
        }

        /// <summary>
        /// Only on: Android
        /// </summary>
        /// <param name="effectId">
        /// A effect integer value ID, based in Android effects
        /// <br/>
        /// <b> See: </b> 
        /// <a href="https://developer.android.com/reference/android/os/VibrationEffect"> Developer Android: VibrationEffect </a>
        /// </param>
        /// <returns>True, if an <paramref name="effectId"/> is supported or False otherwise</returns>
        public virtual bool IsEffectSupported(int effectId)
        {
            #if UNITY_ANDROID
            
            if (vibrationPlugin == null)
            {
                return false;
            }

            bool result = vibrationPlugin.Call<bool>("isEffectSupported", effectId);
            return result;
            
            #else
            
            return false;

            #endif
        }

        /// <summary>
        /// Only on: Android
        /// </summary>
        /// <param name="primitiveId">
        /// A primitive integer value ID, based in Android Composition primitives
        /// <br/>
        /// <b> See: </b> 
        /// <a href="https://developer.android.com/reference/android/os/VibrationEffect.Composition"> Developer Android: VibrationEffect.Composition </a>
        /// </param>
        /// <returns>True, if a <paramref name="primitiveId"/> is supported or False otherwise</returns>
        public virtual bool IsPrimitiveSupported(int primitiveId)
        {
            #if UNITY_ANDROID
            
            if (vibrationPlugin == null)
            {
                return false;
            }

            bool result = vibrationPlugin.Call<bool>("isPrimitiveSupported", primitiveId);
            return result;
            
            #else
            
            return false;

            #endif
        }
        
        public virtual void Cancel()
        {
            #if UNITY_ANDROID && !UNITY_EDITOR
            
            if (vibrationPlugin == null)
            {
                return;
            }

            vibrationPlugin.Call("cancel");

            #else
            
            Vibration.Cancel();
            
            #endif
        }
    }
}
