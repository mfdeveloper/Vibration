using System;
using UnityEngine;
using UAP;
using VibrationPlugin.Enums;
using VibrationPlugin.Save;

namespace VibrationPlugin
{
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
        protected VibrationType type = VibrationType.Light;

        #if UNITY_ANDROID

        protected AndroidJavaObject vibrationPlugin;

        protected AndroidJavaClass unityPlayer;
        protected AndroidJavaObject currentActivity;
        protected AndroidJavaObject context;
        protected AndroidJavaObject audioAttributesBuilder;

        #endif

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

        #endregion

        #region Unity Events

        protected virtual void Awake()
        {
            LoadOptionsSaved();
        }

        // Start is called before the first frame update
        protected virtual void Start()
        {
            #if UNITY_ANDROID && !UNITY_EDITOR

            unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            context = currentActivity.Call<AndroidJavaObject>("getApplicationContext");
            audioAttributesBuilder = new AndroidJavaObject("android.media.AudioAttributes$Builder");
            vibrationPlugin = new AndroidJavaObject("com.benoitfreslon.unity.vibrations.lib.Vibration", context);

            #endif
            
            // Vibrate(milliseconds: (long) duration, type);
        }

        private void OnApplicationPause(bool pauseStatus)
        {
            Vibrate(milliseconds: duration, type);
        }

        protected virtual void OnEnable()
        {
            UAP_AccessibilityManager.RegisterOnSwipeSingleTapCallback(OnSwipe);
        }

        protected virtual void OnDisable()
        {
            #if UNITY_ANDROID

            if (unityPlayer != null)
            {
                unityPlayer.Dispose();
                unityPlayer = null;
            }

            if (vibrationPlugin != null)
            {
                vibrationPlugin.Dispose();
                vibrationPlugin = null;
            }

            if (currentActivity != null)
            {
                currentActivity.Dispose();
                currentActivity = null;
            }
            
            if (context != null)
            {
                context.Dispose();
                context = null;
            }

            #endif
            
            UAP_AccessibilityManager.UnregisterOnSwipeSingleTapCallback(OnSwipe);
        }

        #endregion
        
        private void OnSwipe(UAP_AccessibilityManager.ESDirection direction, float fingerCount)
        {
            // TODO: Call "Vibrate(type)" with a type here!!
            // Vibrate(milliseconds: (long) duration, type);
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
                Debug.LogWarningFormat("[{0}] The parameter '{1}' is required to be defined in inspector", TAG, nameof(options));
            }

            if (PlayerPrefs.HasKey(options.GetType().Name))
            {
                string jsonOptions = PlayerPrefs.GetString(options.GetType().Name);

                // Not use JsonUtility.FromJson() to ScriptableObject or MonoBehaviour objects
                JsonUtility.FromJsonOverwrite(jsonOptions, options);
            }
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

        public virtual void Vibrate()
        {
            Vibration.Vibrate();
        }

        public virtual void Vibrate(long milliseconds)
        {
            if (!IsEnabled)
            {
                return;
            }

            #if UNITY_ANDROID && !UNITY_EDITOR
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }
            
            AndroidJavaObject audioAttributes = audioAttributesBuilder.Call<AndroidJavaObject>("build"); 
            AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibrate", milliseconds, (int) type, audioAttributes);

            if (result != null && Debug.isDebugBuild)
            {
                // TODO: Add a UI text in the scene, with the log below when generate a Development Build
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                
                Debug.Log($"[{TAG}] RESULT: ${result.Call<string>("toString")}, EFFECT: ${this.type}");
            }
            
            #elif UNITY_IOS && !UNITY_EDITOR
            
            Handheld.Vibrate();
            
            #endif
        }
        
        public virtual void Vibrate(VibrationType vibrationType)
        {
            if (!IsEnabled)
            {
                return;
            }

            #if UNITY_ANDROID && !UNITY_EDITOR
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }
            
            AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibrate", (int) vibrationType, null);

            if (result != null && Debug.isDebugBuild)
            {
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                Debug.Log($"[{TAG}] RESULT: ${result.Call<string>("toString")}, EFFECT: ${this.type}");
            }
            
            #elif UNITY_IOS && !UNITY_EDITOR
            
            Handheld.Vibrate();
            
            #endif
        }

        public virtual void Vibrate(long milliseconds, VibrationType vibrationType)
        {
            #if UNITY_ANDROID && !UNITY_EDITOR

            type = vibrationType;
            Vibrate(milliseconds);

            #elif UNITY_IOS && !UNITY_EDITOR

            Handheld.Vibrate();

            #endif
        }
        
        public virtual void Vibrate(long milliseconds, MobileTimeUnit timeUnit)
        {
            
            #if UNITY_ANDROID && !UNITY_EDITOR
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }
                
            AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibrateWithTimeUnit", milliseconds, timeUnit.ToString());
            
            if (result != null && Debug.isDebugBuild)
            {
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                
                Debug.Log($"[{TAG}] RESULT: ${result.Call<string>("toString")}, EFFECT: ${this.type}");
            }

            #elif UNITY_IOS && !UNITY_EDITOR

            Vibration.Vibrate(milliseconds);

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
            
            #if UNITY_ANDROID && !UNITY_EDITOR
            
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin == null)
            {
                return;
            }

            AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibrate", pattern, (int) repeat, null);
        
            if (result != null && Debug.isDebugBuild)
            {
                // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                
                Debug.Log($"[{TAG}] RESULT: ${result.Call<string>("toString")}, EFFECT: ${this.type}");
            }

            #elif UNITY_IOS && !UNITY_EDITOR

            Handheld.Vibrate();

            #endif
        }

        public virtual bool HasVibrator()
        {
            #if UNITY_ANDROID && !UNITY_EDITOR
            
            if (vibrationPlugin == null)
            {
                return false;
            }

            var hasVibrator = vibrationPlugin.Call<bool>("hasVibrator");
            return hasVibrator;

            #elif UNITY_IOS

            return Vibration.HasVibrator();

            #endif

            return false;
        }

        public virtual bool IsEffectSupported(int effectId)
        {
            #if UNITY_ANDROID && !UNITY_EDITOR
            
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
        public virtual bool IsPrimitiveSupported(int primitiveId)
        {
            #if UNITY_ANDROID && !UNITY_EDITOR
            
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

            #endif
        }
    }
}
