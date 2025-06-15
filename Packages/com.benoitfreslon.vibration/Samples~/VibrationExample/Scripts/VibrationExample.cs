////////////////////////////////////////////////////////////////////////////////
//  
// @author Benoît Freslon @benoitfreslon
// https://github.com/BenoitFreslon/Vibration
// https://benoitfreslon.com
//
////////////////////////////////////////////////////////////////////////////////

using System;
using UnityEngine;
using UnityEngine.UI;
using VibrationPlugin.Mobile;

namespace VibrationPlugin.Samples
{
    [DisallowMultipleComponent]
    public class VibrationExample : MonoBehaviour
    {
        public Text inputTime;
        public Text inputPattern;
        public Text inputRepeat;
        public Text txtAndroidVersion;

        // Use this for initialization
        void Start ()
        {
            Init();
        }

        public void Init() 
        {
            if (Debug.isDebugBuild)
            {
                Debug.Log($"Application.isMobilePlatform: {Application.isMobilePlatform}");
            }

            if (txtAndroidVersion != null)
            {
                txtAndroidVersion.text = $"Android Version: {AndroidBuild.VersionText}";
            }
        }

        public void TapVibrate()
        {
            Vibration.Vibrate();
        }

        public void TapVibrateCustom()
        {
            Vibration.Vibrate(milliseconds: int.Parse(inputTime.text));
        }

        public void TapVibratePattern()
        {
            string[] patterns = inputPattern.text.Replace(" ", "" ).Split(',');
            long[] longs = Array.ConvertAll(patterns, long.Parse);

            Debug.Log(longs.Length);
            
            Vibration.Vibrate(pattern: longs, repeat: int.Parse(inputRepeat.text));
        }

        public void TapCancelVibrate()
        {
            Vibration.Cancel();
        }

        public void TapPopVibrate()
        {
            Vibration.VibratePop();
        }

        public void TapPeekVibrate()
        {
            Vibration.VibratePeek();
        }

        public void TapNopeVibrate()
        {
            Vibration.VibrateNope();
        }
    }   
}
