package com.example.sanbotdemo

import com.sanbot.opensdk.base.BindBaseService
import com.sanbot.opensdk.function.unit.HardWareManager
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

    override fun onMainServiceConnected() {
        hardWareManager.setOnHareWareListener(this)
        serviceScope.launch {
            sanbot.onConnected()
        }
    }

    override fun gyroscopeCheckResult(accelerometerStatus: Boolean, compassStatus: Boolean) {

    }

    override fun gyroscopeData(driftAngle: Float, elevationAngle: Float, rollAngle: Float) {

    }

}