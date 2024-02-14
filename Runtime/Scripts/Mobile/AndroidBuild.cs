using UnityEngine;

namespace VibrationPlugin.Mobile
{    
    public static class AndroidBuild 
    {
        #region Properties

        private static int version = -1;

        /// <summary>
        /// Android native SDK_INT version implementation
        /// from Java static class: <i>android.os.Build.VERSION</i>
        /// </summary>
        /// <value></value>
        public static int Version
        {
            get 
            {
                #if	UNITY_ANDROID
                
                if (version == -1 || version == 0)
                {
                    using AndroidJavaClass buildVersionClass = new("android.os.Build$VERSION");
                    version = buildVersionClass.GetStatic<int>("SDK_INT");
                }

                #endif
                
                return version;
            }
        }

        /// <summary>
        /// Player OS version using <see cref="UnityEngine.SystemInfo"/> class 
        /// from Unity <i>C#</i> layer.
        /// </summary>
        /// <remarks>
        /// <b>PS:</b> You can use this property instead of <see cref="Version"/> if you prefer
        /// an Unity <i>C#</i> approach
        /// </remarks>
        /// <value></value>
        public static int SystemVersion 
        { 
            get 
            {
                if (Application.platform == RuntimePlatform.Android 
                    && version == -1 
                    || version == 0) {
                    string androidVersion = SystemInfo.operatingSystem;
                    int sdkPos = androidVersion.IndexOf( "API-" );
                    version = int.Parse(androidVersion.Substring(sdkPos + 4, 2).ToString());
                }

                return version;
            }
        }

        /// <summary>
        /// The <see cref="Version"/> for output texts (e.g Unity UI texts, Log messages...)
        /// </summary>
        public static string VersionText => Version != -1 ? Version.ToString() : "[Platform unsupported]";

        /// <summary>
        /// An alias of property <see cref="Version"/>
        /// </summary>
        public static int SdkInt => Version;

        #endregion    
    }
}
