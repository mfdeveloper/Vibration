namespace VibrationPlugin.Enums
{
    public enum VibrationType
    {
        /// <summary>
        /// Not use an pre-defined type/effect, but a <i>duration</i> (in milliseconds) instead
        /// </summary>
        None = -2,
        /// <summary>
        /// The default Unity effect that calls <see cref="UnityEngine.Handheld.Vibrate"/>
        /// </summary>
        HandheldDefault = -1,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 5], <br/>
        /// amplitude = [0, 50]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [25, 50, 25, 50, 25]
        /// </summary>
        Tick,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 100, 50], <br/>
        /// amplitude = [30, 0, 30]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [25, 100, 25, 100, 25]
        /// </summary>
        LowTick,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// fallbackDuration = 20
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [25, 100, 25, 100, 25] 
        /// </summary>
        Short,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// The same values of <see cref="Tick"/>
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// The same values of <see cref="Tick"/>
        /// </summary>
        Light,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// duration = 1000
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// duration = 1000
        /// </summary>
        Normal,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 10] <br/>
        /// amplitude = [0, 180]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [50]
        /// </summary>
        Click,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 75, 75, 75]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [50, 100, 50]
        /// </summary>
        DoubleTap,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// The same values of <see cref="DoubleTap"/>
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// The same values of <see cref="DoubleTap"/>
        /// </summary>
        DoubleClick,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 30, 50] <br/>
        /// amplitude = [255, 0, 255]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [200]
        /// </summary>
        Heavy,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 100] <br/>
        /// amplitude = [255, 0]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [100, 100, 100, 100]
        /// </summary>
        Thud,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 40, 20, 40] <br/>
        /// amplitude = [255, 0, 255, 0]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [200, 100, 200, 100, 200, 100]
        /// </summary>
        Spin,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 10, 30] <br/>
        /// amplitude = [255, 0, 255]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
        /// </summary>
        QuickRise,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 100, 100] <br/>
        /// amplitude = [255, 0, 255]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [50, 40, 30, 20, 10]
        /// </summary>
        SlowRise,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// pattern = [0, 30, 10] <br/>
        /// amplitude = [255, 0, 255]
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// pattern = [100, 90, 80, 70, 60, 50, 40, 30, 20, 10]
        /// </summary>
        QuickFall,
        /// <summary>
        /// Native mobile fallback values (patterns, amplitude and/or milliseconds duration)
        /// <br/><br/>
        /// <b> ANDROID </b>
        /// <br/>
        /// A custom composition of effects: <see cref="SlowRise"/>, <see cref="QuickFall"/> and <see cref="Tick"/>
        /// patterns and amplitudes
        /// <br/> <br/>
        /// <b> WebGL </b>
        /// <br/>
        /// A custom composition of effects: <see cref="SlowRise"/>, <see cref="QuickFall"/> and <see cref="Tick"/>
        /// patterns and timeouts used with <i>setTimeOut()</i> Javascript function.
        /// </summary>
        GrowsIntensityDiesOff
    }
}
