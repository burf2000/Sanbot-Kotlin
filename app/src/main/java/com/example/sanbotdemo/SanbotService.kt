package com.example.sanbotdemo

import com.sanbot.opensdk.base.BindBaseService
import com.sanbot.opensdk.function.unit.HardWareManager
import com.sanbot.opensdk.function.unit.SpeechManager
import com.sanbot.opensdk.function.unit.WheelMotionManager
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener

interface SanbotServiceInterface {
    fun speak(text: String)
}

class SanbotService : BindBaseService(), GyroscopeListener, SanbotServiceInterface {

    private val sanbot: Sanbot = SanbotComponent.create().sanbot
    private val hardWareManager = HardWareManager(this)
    private val speechManager = SpeechManager(this)
    private val wheelMotionManager = WheelMotionManager(this)

    override fun speak(text: String) {
        speechManager.startSpeak(text)
    }

    override fun onCreate() {
        register(SanbotService::class.java)
        super.onCreate()
    }

    override fun onMainServiceConnected() {
        hardWareManager.setOnHareWareListener(this)
        sanbot.onConnected(this)
    }

    override fun gyroscopeCheckResult(accelerometerStatus: Boolean, compassStatus: Boolean) {

    }

    override fun gyroscopeData(driftAngle: Float, elevationAngle: Float, rollAngle: Float) {

    }

}