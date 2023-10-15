////////////////////////////////////////////////////////////////////////////////
//
// @author Benoît Freslon @benoitfreslon
// https://github.com/BenoitFreslon/Vibration
// https://benoitfreslon.com
//
////////////////////////////////////////////////////////////////////////////////

#if UNITY_WEBGL || (UNITY_IOS && !UNITY_EDITOR)
using System;
using System.Runtime.InteropServices;
using System.Collections;
using VibrationPlugin.Enums;
#endif
using UnityEngine;

namespace VibrationPlugin
{
	public static class Vibration
	{
		#if UNITY_IOS && !UNITY_EDITOR
		
	    [DllImport ( "__Internal" )]
	    private static extern bool _HasVibrator ();

	    [DllImport ( "__Internal" )]
	    private static extern void _Vibrate ();

	    [DllImport ( "__Internal" )]
	    private static extern void _VibratePop ();

	    [DllImport ( "__Internal" )]
	    private static extern void _VibratePeek ();

	    [DllImport ( "__Internal" )]
	    private static extern void _VibrateNope ();
		
		#endif

		#if UNITY_WEBGL
				
		[DllImport("__Internal")]
		public static extern void Vibrate(int milliseconds, int typeValue = (int) VibrationType.None);
        
		[DllImport("__Internal")]
		public static extern void VibrateWithPattern(int[] pattern, int patternSize, int typeValue = (int) VibrationType.None, int repeat = -1);
        
		[DllImport("__Internal")]
		public static extern void VibrateCancel();

		[DllImport("__Internal")]
		public static extern int IsVibrateSupported();
		
		#endif

		#if UNITY_ANDROID && !UNITY_EDITOR
		
		public static AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
		public static AndroidJavaObject currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
		public static AndroidJavaObject vibrator = currentActivity.Call<AndroidJavaObject>("getSystemService", "vibrator");
		public static AndroidJavaObject context = currentActivity.Call<AndroidJavaObject>("getApplicationContext");
		
		#endif

		///<summary>
		/// Tiny pop vibration
		///</summary>
		public static void VibratePop()
		{
			
			#if UNITY_IOS && !UNITY_EDITOR
			
	        _VibratePop();
				
			#elif (UNITY_ANDROID || UNITY_WEBGL) && !UNITY_EDITOR
			
			Vibrate(15);
			
			#endif
			
		}

		///<summary>
		/// Small peek vibration
		///</summary>
		public static void VibratePeek()
		{
			#if UNITY_IOS && !UNITY_EDITOR
			
			_VibratePeek();
				
			#elif (UNITY_ANDROID || UNITY_WEBGL) && !UNITY_EDITOR
			
			Vibrate(25);
				
			#endif
			
		}

		///<summary>
		/// 3 small vibrations
		///</summary>
		public static void VibrateNope()
		{
			#if UNITY_IOS && !UNITY_EDITOR
			
			_VibrateNope();
			
			#elif (UNITY_ANDROID || UNITY_WEBGL) && !UNITY_EDITOR
			
			long[] pattern = { 0, 5, 5, 5 };
			Vibrate(pattern);
			
			#endif
		}

		public static void Vibrate()
		{
			// TODO: [Improvement] Implement joystick vibration for PC platform 
			// 		 (e.g A player using a joystick on Steam)
			
			#if UNITY_ANDROID || UNITY_IOS
			
			Handheld.Vibrate();

			#elif UNITY_WEBGL

			Vibrate(0, typeValue: (int) VibrationType.Normal);
			
			#else

			if (Application.isConsolePlatform)
			{
				Handheld.Vibrate();
			}

			#endif
		}
		
		/// <summary>
		/// Only on: Android and WebGL
		/// </summary>
		/// <remarks>
		/// <b> References: </b>
		/// <br/><br/>
		/// <ul>
		/// 	<li> 
		/// 		<a href="https://developer.android.com/reference/android/os/Vibrator.html#vibrate(long)"> 
		/// 			Developers Android: Vibrator.vibrate(long) 
		/// 		</a> 
		/// 	<br/>
		/// 	</li>
		/// 	<li> 
		/// 		<a href="https://developer.mozilla.org/en-US/docs/Web/API/Navigator/vibrate"> 
		/// 			Web/HTML5: Navigator vibrate() method
		/// 		</a> 
		/// 	</li>
		/// </ul>
		/// </remarks>
		public static void Vibrate(long milliseconds)
		{
			#if UNITY_ANDROID && !UNITY_EDITOR

			vibrator.Call("vibrate", milliseconds);

			#elif UNITY_IOS && !UNITY_EDITOR

			Handheld.Vibrate();

			#elif UNITY_WEBGL

			int intMilliseconds = Convert.ToInt32(milliseconds);
			Vibrate(intMilliseconds, typeValue: (int) VibrationType.None);

			#endif
		}

		/// <summary>
		/// Only on: Android and WebGL
		/// </summary>
		/// <remarks>
		/// <b> References: </b>
		/// <br/><br/>
		/// <ul>
		/// 	<li> 
		/// 		<a href="https://proandroiddev.com/using-vibrate-in-android-b0e3ef5d5e07"> 
		/// 			Using Vibrate In Android 
		/// 		</a> 
		/// 	<br/>
		/// 	</li>
		/// 	<li> 
		/// 		<a href="https://developer.mozilla.org/en-US/docs/Web/API/Navigator/vibrate"> 
		/// 			Web/HTML5: Navigator vibrate() method
		/// 		</a> 
		/// 	</li>
		/// </ul>
		/// </remarks>
		public static void Vibrate(long[] pattern, int repeat = -1)
		{
			#if UNITY_ANDROID && !UNITY_EDITOR

			vibrator.Call("vibrate", pattern, repeat);

			#elif UNITY_IOS && !UNITY_EDITOR

			Handheld.Vibrate();

			#elif UNITY_WEBGL

			var intPattern = Array.ConvertAll(pattern, Convert.ToInt32);
			VibrateWithPattern(intPattern, intPattern.Length, typeValue: (int) VibrationType.None, repeat);

			#endif
		}

		/// <summary>
		/// Only on: Android and WebGL
		/// </summary>
		/// <remarks>
		/// <b> References: </b>
		/// <br/><br/>
		/// <ul>
		/// 	<li> 
		/// 		<a href="https://developer.android.com/reference/android/os/Vibrator#cancel()"> 
		/// 			Developers Android: Vibrator.cancel()
		/// 		</a> 
		/// 	<br/>
		/// 	</li>
		/// 	<li> 
		/// 		<a href="https://developer.mozilla.org/en-US/docs/Web/API/Navigator/vibrate#parameters"> 
		/// 			Web/HTML5: Navigator vibrate() cancel
		/// 		</a> 
		/// 	</li>
		/// </ul>
		/// </remarks> 
		public static void Cancel()
		{
			#if UNITY_ANDROID && !UNITY_EDITOR
			
			vibrator.Call("cancel");

			#elif UNITY_WEBGL

			VibrateCancel();
			
			#endif
		}

		public static bool HasVibrator()
		{
			#if UNITY_ANDROID && !UNITY_EDITOR
			
			AndroidJavaClass contextClass = new AndroidJavaClass("android.content.Context");
			string Context_VIBRATOR_SERVICE = contextClass.GetStatic<string>("VIBRATOR_SERVICE");
			AndroidJavaObject systemService = context.Call<AndroidJavaObject>("getSystemService", Context_VIBRATOR_SERVICE);
			
			return systemService.Call<bool>("hasVibrator");
			
			#elif UNITY_IOS && !UNITY_EDITOR
			
			return _HasVibrator ();

			#elif UNITY_WEBGL

			return IsVibrateSupported() == 1;
			
			#else
			
			return false;
			
			#endif
		}
	}
}
