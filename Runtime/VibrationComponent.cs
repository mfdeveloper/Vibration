using UnityEngine;

public class VibrationComponent : MonoBehaviour
{
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
    protected float duration = 2;

    [SerializeField]
    [Tooltip("The type of vibration effect (SHORT: Android <= 9, LIGHT...HEAVY: Android >= 10)")]
    protected VibrationType type = VibrationType.Short;

    #if UNITY_ANDROID
    
    public AndroidJavaObject vibrationPlugin;

    public AndroidJavaClass unityPlayer;
    public AndroidJavaObject currentActivity;
    public AndroidJavaObject context;

    #endif
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

    protected virtual void Awake()
    {

        if (options == null)
        {
            /*
             * Fallback to some Unity versions that ScriptableObjects 
             * passed by inspector is disappear when plays.
             * 
             * Or, if a developer forget to define a custom ScriptableObject
             * to save options on inspector
             */
            options = ScriptableObject.CreateInstance<Options>();
            Debug.LogWarningFormat("The parameter '{0}' is required to be defined in inspector", nameof(options));
        }

        if (PlayerPrefs.HasKey(options.GetType().Name))
        {
            string jsonOptions = PlayerPrefs.GetString(options.GetType().Name);

            // Not use JsonUtility.FromJson() to ScriptableObject or Monobehavior objects
            JsonUtility.FromJsonOverwrite(jsonOptions, options);
        }
    }

    // Start is called before the first frame update
    protected virtual void Start()
    {

        #if UNITY_ANDROID && !UNITY_EDITOR

        unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        context = currentActivity.Call<AndroidJavaObject>("getApplicationContext");
        vibrationPlugin = new AndroidJavaObject("com.benoitfreslon.unity.vibrations.lib.Vibration", context);
        
        #endif
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
    }

    ///<summary>
    /// Tiny pop vibration
    ///</summary>
    public virtual void VibratePop()
    {
        #if UNITY_IOS && !UNITY_EDITOR
        
        Vibration.VibratePop();
        
        #elif UNITY_ANDROID
		
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
        
        #elif UNITY_ANDROID
		
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
        
        #elif UNITY_ANDROID
        
		long [] pattern = { 0, 5, 5, 5 };
		Vibrate(pattern, VibrationRepeat.NO_REPEAT);
        
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

        #if !UNITY_WEBGL
            #if UNITY_ANDROID
                AndroidJNIHelper.debug = true;

                if (vibrationPlugin != null)
                {
                    AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibr", milliseconds, (int) this.type, null);

                    if (result != null)
                    {
                        // TODO: Add a UI text in the scene, with the log below when generate a Development Build
                        // To see this log, connect your phone with the USB cable, open Android Studio => LogCat panel
                    #if DEVELOPMENT_BUILD
                        Debug.Log($"RESULT: ${result.Call<string>("toString")}, EFFECT: ${this.type}");
                    #endif
                    }
                }
            #elif UNITY_IOS && !UNITY_EDITOR
                Handheld.Vibrate();
            #endif
        #endif
    }

    public virtual void Vibrate(long milliseconds, VibrationType vibrationType)
    {
        #if !UNITY_WEBGL
            #if UNITY_ANDROID
            
            this.type = vibrationType;
            Vibrate(milliseconds);
            
            #elif UNITY_IOS && !UNITY_EDITOR
            
            Handheld.Vibrate();
        
            #endif
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

        #if !UNITY_WEBGL
        
            #if UNITY_ANDROID
            
            if (vibrationPlugin != null)
            {
                AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibr", pattern, (int) repeat, null);
            }
            
            #elif UNITY_IOS && !UNITY_EDITOR
            
            Handheld.Vibrate();
            
            #endif
        
        #endif
    }

    public virtual bool HasVibrator()
    {
        return Vibration.HasVibrator();
    }
}
