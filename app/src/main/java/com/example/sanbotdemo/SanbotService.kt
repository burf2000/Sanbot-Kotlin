package com.example.sanbotdemo

import android.util.Log
import com.sanbot.opensdk.base.BindBaseService
import com.sanbot.opensdk.function.beans.LED
import com.sanbot.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion
import com.sanbot.opensdk.function.unit.HardWareManager
import com.sanbot.opensdk.function.unit.HeadMotionManager
import com.sanbot.opensdk.function.unit.SpeechManager
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SanbotService : BindBaseService(), GyroscopeListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var sanbot: Sanbot
    private val hardWareManager = HardWareManager(this)
    private val speechManager = SpeechManager(this)
    private val headMotionManager = HeadMotionManager(this)

    override fun onCreate() {
        register(SanbotService::class.java)
        super.onCreate()
        sanbot = (application as SanbotApplication).sanbot
        serviceScope.launch {
            sanbot.toSpeak.collect { speak(it) }
        }
    }

    private fun speak(text: String) {
        if (text.isNotEmpty()) {
            speechManager.startSpeak(text)
        }
    }

    private fun flickerColours() {
        val led = LED(
            LED.PART_ALL,
            LED.MODE_FLICKER_RANDOM,
            10, // delayTime (100ms units)
            3   // random color count
        )
        hardWareManager.setLED(led)
    }

    private fun moveHead() {
        val motion = AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, 90)
        headMotionManager.doAbsoluteAngleMotion(motion)
    }

    override fun onMainServiceConnected() {
        hardWareManager.setOnHareWareListener(this)
        serviceScope.launch {
            sanbot.onConnected()
        }
    }

    override fun gyroscopeCheckResult(
        accelerometerStatus: Boolean,
        compassStatus: Boolean
    ) {
        serviceScope.launch {
            sanbot.gyroscopeCheckResult.emit(GyroscopeCheckResult(accelerometerStatus, compassStatus))
        }
    }

    override fun gyroscopeData(
        driftAngle: Float,
        elevationAngle: Float,
        rollAngle: Float
    ) {
        serviceScope.launch {
            sanbot.gyroscopeData.emit(GyroscopeData(driftAngle, elevationAngle, rollAngle))
        }
    }

}