package com.example.sanbotdemo

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.sanbot.opensdk.base.BindBaseService
import com.sanbot.opensdk.beans.FuncConstant
import com.sanbot.opensdk.function.beans.EmotionsType
import com.sanbot.opensdk.function.beans.FaceRecognizeBean
import com.sanbot.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion
import com.sanbot.opensdk.function.beans.speech.Grammar
import com.sanbot.opensdk.function.beans.speech.RecognizeTextBean
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion
import com.sanbot.opensdk.function.unit.HDCameraManager
import com.sanbot.opensdk.function.unit.HardWareManager
import com.sanbot.opensdk.function.unit.HeadMotionManager
import com.sanbot.opensdk.function.unit.ModularMotionManager
import com.sanbot.opensdk.function.unit.SpeechManager
import com.sanbot.opensdk.function.unit.SystemManager
import com.sanbot.opensdk.function.unit.WheelMotionManager
import com.sanbot.opensdk.function.unit.WingMotionManager
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener
import com.sanbot.opensdk.function.unit.interfaces.hardware.InfrareListener
import com.sanbot.opensdk.function.unit.interfaces.hardware.ObstacleListener
import com.sanbot.opensdk.function.unit.interfaces.hardware.PIRListener
import com.sanbot.opensdk.function.unit.interfaces.hardware.TouchSensorListener
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SanbotService : BindBaseService(),   FaceRecognizeListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var sanbot: Sanbot

    internal lateinit var hardWareManager: HardWareManager
    internal lateinit var speechManager: SpeechManager
    internal lateinit var headMotionManager: HeadMotionManager
    internal lateinit var wheelManager: WheelMotionManager
    internal lateinit var wingMotionManager: WingMotionManager
    internal lateinit var systemManager: SystemManager
    internal lateinit var cameraManager: HDCameraManager
    internal lateinit var modularMotionManager : ModularMotionManager

    private lateinit var wakeLock: PowerManager.WakeLock

    var TAG = "Sanbot"

    private var isSayingHello = false

    override fun onCreate() {
        register(SanbotService::class.java)

        super.onCreate()
        sanbot = (application as SanbotApplication).sanbot

        //TODO: Need to understand what this does
        serviceScope.launch { sanbot.toSpeak.collect { speak(it) } }
        serviceScope.launch { sanbot.flickerColours.collect { flickerColours(it) } }
        serviceScope.launch { sanbot.reset.collect { reset(it) } }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
            "SanbotService::ScreenWakeLock"
        )
        wakeLock.acquire()
    }

    override fun onDestroy() {
        super.onDestroy()

        hardWareManager.setOnHareWareListener(null)
        speechManager.setOnSpeechListener(null)


        if (::wakeLock.isInitialized && wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    override fun onMainServiceConnected() {

        hardWareManager = getUnitManager(FuncConstant.HARDWARE_MANAGER) as HardWareManager
        speechManager = getUnitManager(FuncConstant.SPEECH_MANAGER) as SpeechManager
        headMotionManager = getUnitManager(FuncConstant.HEADMOTION_MANAGER) as HeadMotionManager
        wheelManager = getUnitManager(FuncConstant.WHEELMOTION_MANAGER) as WheelMotionManager
        wingMotionManager = getUnitManager(FuncConstant.WINGMOTION_MANAGER) as WingMotionManager
        systemManager = getUnitManager(FuncConstant.SYSTEM_MANAGER) as SystemManager
        cameraManager = getUnitManager(FuncConstant.HDCAMERA_MANAGER) as HDCameraManager
        modularMotionManager = getUnitManager(FuncConstant.MODULARMOTION_MANAGER) as ModularMotionManager

        setupGyro()
        setupListen()
        setupTouch()

        cameraManager.setMediaListener(this)
        speak("Loaded")

        serviceScope.launch {
            sanbot.onConnected()
        }

        reset(true)
        moveHeadUp(60)
    }

    private fun setupTouch() {

        hardWareManager.setOnHareWareListener(object : TouchSensorListener {
            override fun onTouch(part: Int) {
                Log.i(TAG ,"touched $part")
                //speechManager.startSpeak("Hello")

                serviceScope.launch(Dispatchers.Main) {
                    speechManager.doWakeUp()
                }
            }

            override fun onTouch(part: Int, isTouch: Boolean) {
                Log.i(TAG ,"touched $part: $isTouch")
            }
        })

        hardWareManager.setOnHareWareListener(object : InfrareListener {
            override fun infrareDistance(part: Int, distance: Int) {
                //Log.i(TAG, "Infrared Object detected close at sensor $part: $distance cm")
            }
        })

        hardWareManager.setOnHareWareListener(object : PIRListener {
            override fun onPIRCheckResult(isChecked: Boolean, part: Int) {
                //Log.i(TAG, "PIR Motion detected by sensor $part $isChecked")
            }
        })

        hardWareManager.setOnHareWareListener(object : ObstacleListener {
            override fun onObstacleStatus(status: Boolean) {
                Log.i(TAG, "Obstacle $status")
            }
        })
    }

    private fun setupGyro() {
        hardWareManager.setOnHareWareListener(object : GyroscopeListener {
            override fun gyroscopeCheckResult(
                accelerometerStatus: Boolean,
                compassStatus: Boolean
            ) {
                serviceScope.launch {
                    Log.i(TAG, " Gyroscope Acc: $accelerometerStatus, Compass: $compassStatus")

                    sanbot.gyroscopeCheckResult.emit(GyroscopeCheckResult(accelerometerStatus, compassStatus))
                }
            }

            override fun gyroscopeData(driftAngle: Float, elevationAngle: Float, rollAngle: Float) {
                serviceScope.launch {
                    Log.i(TAG, " Gyroscope Drift: $driftAngle, Elevation: $elevationAngle, Roll: $rollAngle")
                    sanbot.gyroscopeData.emit(GyroscopeData(driftAngle, elevationAngle, rollAngle))
                }
            }
        })
    }

    private fun setupListen() {

            //Set wakeup, sleep callback
        speechManager.setOnSpeechListener(object : WakenListener {
                override fun onWakeUpStatus(b: Boolean) {
                    Log.i(TAG, "Listen wake status $b")
                }

                override fun onWakeUp() {
                    Log.i(TAG, "Listen wake")
                }

                override fun onSleep() {
                    Log.i(TAG, "Listen wake sleep")

                    serviceScope.launch(Dispatchers.Main) {
                        delay(1000)
                        speechManager.doWakeUp()
                    }
                }
            })

        speechManager.setOnSpeechListener(object : RecognizeListener {
            override fun onError(engine: Int, errorCode: Int) {

                Log.i(TAG, "Listen Error $Int")
            }

            override fun onRecognizeResult(grammar: Grammar): Boolean {
                val heard = grammar.text
                Log.i(TAG, "Listen Heard $heard")

                //speechManager.startSpeak("I heard $heard")
//                when {
//                    heard?.contains("hello", ignoreCase = true) ?:  -> {
//                        speechManager.startSpeak("Hello, human!")
//                    }
//                    heard?.contains("go forward", ignoreCase = true) ?:  -> {
//                        // Move robot forward (using WheelMotionManager)
//                    }
//                }

                // true = Sanbot won’t handle it further (you take over)

                return true
            }

            override fun onRecognizeText(recognizeText: RecognizeTextBean) {
                Log.i(TAG, "Listen Heard2 ${recognizeText.text}")
            }

            override fun onRecognizeVolume(volume: Int) {

                //Log.i(TAG, "Listen Volume $volume")
            }

            override fun onStartRecognize() {
                Log.i(TAG, "Listen started")
            }

            override fun onStopRecognize() {
                Log.i(TAG, "Listen stopped")
            }

        })
    }

    private fun speak(text: String) {
        if (text.isNotEmpty()) {
            speechManager.startSpeak(text)
            Log.i(TAG, "Said $text")
        }
    }
    
    private fun moveHeadUp(up : Int) {
        val lookUpMotion = AbsoluteAngleHeadMotion(
            AbsoluteAngleHeadMotion.ACTION_VERTICAL,  // 👈 Vertical action
            up  // Degrees (0 = center, positive = up)
        )
        headMotionManager.doAbsoluteAngleMotion(lookUpMotion)
    }

    private fun flickerColours(flicker: Boolean) {
        //speechManager.doWakeUp()

        serviceScope.launch(Dispatchers.Main) {
            delay(1000)
            speechManager.doWakeUp()
        }

//        if (flicker) {
//            val led = LED(
//                LED.PART_ALL,
//                LED.MODE_FLICKER_RANDOM,
//                10, // delayTime (100ms units)
//                3   // random color count
//            )
//
//            hardWareManager.setLED(led)
//            // No callback from the lib, so faking it,
//            // just in case you want UI feedback.
//            serviceScope.launch {
//                delay(timeMillis = 1000)
//                sanbot.flickerColours.emit(value = false)
//            }
//        }


    }

    fun reset(noNeeded: Boolean) {
        serviceScope.launch {

            // reset arms
            val motion = AbsoluteAngleWingMotion(
                AbsoluteAngleWingMotion.PART_BOTH,
                5,
                180
            )
            wingMotionManager.doAbsoluteAngleMotion(motion)

            // Reset head
            val horizontalMotion = AbsoluteAngleHeadMotion(
                AbsoluteAngleHeadMotion.ACTION_HORIZONTAL,
                90
            )
            headMotionManager.doAbsoluteAngleMotion(horizontalMotion)

            delay(1000) // ⏳ Wait 1 second
        }
    }

    fun waveHello() {

        if (isSayingHello) return
        isSayingHello = true

        // Smile!
        systemManager.showEmotion(EmotionsType.SMILE)

        // Say Hello
        speechManager.startSpeak("Hello, nice to meet you!")

        // Coroutine for timing the arm wave
        serviceScope.launch {
            repeat(4) {  // Wave 3 times
                // Arm UP
                val upMotion = AbsoluteAngleWingMotion(
                    AbsoluteAngleWingMotion.PART_RIGHT,
                    5,
                    45
                )
                wingMotionManager.doAbsoluteAngleMotion(upMotion)
                delay(1000)  // Wait 0.6 seconds

                // Arm DOWN
                val downMotion = AbsoluteAngleWingMotion(
                    AbsoluteAngleWingMotion.PART_RIGHT,
                    5,
                    90
                )
                wingMotionManager.doAbsoluteAngleMotion(downMotion)
                delay(1000)  // Wait 0.6 seconds
            }

            // Reset arm to neutral after waving
            val downMotion = AbsoluteAngleWingMotion(
                AbsoluteAngleWingMotion.PART_RIGHT,
                5,
                180
            )
            wingMotionManager.doAbsoluteAngleMotion(downMotion)

            delay(3000)
            isSayingHello = false

        }
    }

    override fun recognizeResult(faceRecognizeBean: List<FaceRecognizeBean>) {

        faceRecognizeBean.forEach {
            Log.i(TAG, " face Detected: ${it.user} (${it.gender})")

            waveHello()
        }
    }
}