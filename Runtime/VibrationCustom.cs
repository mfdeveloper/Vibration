using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VibrationCustom : MonoBehaviour
{
#if UNITY_ANDROID
    public AndroidJavaObject vibrationPlugin;

    public AndroidJavaClass unityPlayer;
    public AndroidJavaObject currentActivity;
#endif

    // Start is called before the first frame update
    void Start()
    {
#if UNITY_ANDROID && !UNITY_EDITOR

        unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        vibrationPlugin = new AndroidJavaObject("com.benoitfreslon.unity.vibrations.lib.Vibration", currentActivity);
#endif
    }

    // Update is called once per frame
    void Update()
    {
        
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

    public virtual void Vibrate(long milliseconds)
    {
#if !UNITY_WEBGL
    #if UNITY_ANDROID
            AndroidJNIHelper.debug = true;

            if (vibrationPlugin != null)
            {
                // TODO: Pass a C# ENUM like a integer in second parameter of vibr() Kotlin method
                AndroidJavaObject result = vibrationPlugin.Call<AndroidJavaObject>("vibr", milliseconds, null, null);
                if (result != null)
                {
                    Debug.Log(result.Call<string>("typeName"));
                }
            }
    #endif
#endif
    }
}
