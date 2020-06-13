using UnityEngine;

public class VibrationComponent : MonoBehaviour
{
    [Tooltip("Enable/Disable vibration for all in the game")]
    public bool enable = true;

    [Header("Save and Load")]
    [Tooltip("ScriptableObject with attributes when enable/disable vibration config (use PlayerPrefs to save from a Menu, for example)")]
    public Options options;

    [Header("Effect")]
    [Tooltip("Vibration duration in seconds")]
    public float duration = 2;

    [Tooltip("The type of vibration effect (SHORT: Android <= 9, LIGHT...HEAVY: Android >= 10)")]
    public VibrationType type = VibrationType.SHORT;

#if UNITY_ANDROID
    public AndroidJavaObject vibrationPlugin;

    public AndroidJavaClass unityPlayer;
    public AndroidJavaObject currentActivity;

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

    protected GameObject dialog = null;

    void Awake()
    {
        if (options != null && PlayerPrefs.HasKey(options.GetType().Name))
        {
            string jsonOptions = PlayerPrefs.GetString(options.GetType().Name);

            // Not use JsonUtility.FromJson() to ScriptableObject or Monobehavior objects
            JsonUtility.FromJsonOverwrite(jsonOptions, options);
        }
    }

    // Start is called before the first frame update
    void Start()
    {

#if UNITY_ANDROID && !UNITY_EDITOR

        unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        vibrationPlugin = new AndroidJavaObject("com.benoitfreslon.unity.vibrations.lib.Vibration", currentActivity);
#endif
    }

    void OnDisable()
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
                // To see this log, conect your phone with the USB cable, open Android Studio => LogCat panel
            #if DEVELOPMENT_BUILD
                Debug.Log($"RESULT: ${result.Call<string>("toString")}, EFFECT: ${this.type}");
            #endif
            }
        }
    #elif UNITY_IOS && !UNITY_EDITOR
        Handheld.Vibrate();
    #else
		Handheld.Vibrate();
    #endif
#endif
    }

    public virtual void Vibrate(long milliseconds, VibrationType type)
    {
#if !UNITY_WEBGL
#if UNITY_ANDROID
        this.type = type;
        Vibrate(milliseconds);
    #elif UNITY_IOS && !UNITY_EDITOR
        Handheld.Vibrate();
    #else
		Handheld.Vibrate();
    #endif
#endif
    }

    ///<summary>
	/// Only on Android
	///</summary>
    ///<see cref="https://proandroiddev.com/using-vibrate-in-android-b0e3ef5d5e07"/>
	public virtual void Vibrate(long[] pattern, VibrationRepeat repeat = VibrationRepeat.NO_REPEAT)
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
    #else
        Handheld.Vibrate();
    #endif
#endif
    }

    public virtual bool HasVibrator()
    {
        return Vibration.HasVibrator();
    }
}
