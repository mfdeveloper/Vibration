package com.benoitfreslon.unity.vibrations.plugin

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.benoitfreslon.unity.vibrations.lib.Vibration
import com.benoitfreslon.unity.vibrations.lib.VibrationType

class MainActivity : AppCompatActivity() {

    lateinit var vibration: Vibration

    // TODO: Verify touch event and execute Vibration.vibrate() method,
    //  depending of the Android API
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibration = Vibration(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return when(event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val result =  vibration.vibr(milliseconds = 20, type = VibrationType.LIGHT)
                Log.w("VibrationPlugin", result.typeName())
                true
            }
            else -> super.onTouchEvent(event)
        }
    }
}
