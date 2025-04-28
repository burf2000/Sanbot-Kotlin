package com.example.sanbotdemo

import android.util.Log
import com.sanbot.opensdk.base.BindBaseService
import com.sanbot.opensdk.beans.FuncConstant
import com.sanbot.opensdk.function.beans.EmotionsType
import com.sanbot.opensdk.function.beans.FaceRecognizeBean
import com.sanbot.opensdk.function.beans.LED
import com.sanbot.opensdk.function.beans.StreamOption
import com.sanbot.opensdk.function.beans.headmotion.AbsoluteAngleHeadMotion
import com.sanbot.opensdk.function.beans.speech.Grammar
import com.sanbot.opensdk.function.beans.speech.RecognizeTextBean
import com.sanbot.opensdk.function.beans.wheelmotion.NoAngleWheelMotion
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion
import com.sanbot.opensdk.function.beans.wing.NoAngleWingMotion
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion
import com.sanbot.opensdk.function.unit.HDCameraManager
import com.sanbot.opensdk.function.unit.HardWareManager
import com.sanbot.opensdk.function.unit.HeadMotionManager
import com.sanbot.opensdk.function.unit.SpeechManager
import com.sanbot.opensdk.function.unit.SystemManager
import com.sanbot.opensdk.function.unit.WheelMotionManager
import com.sanbot.opensdk.function.unit.WingMotionManager
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener
import com.sanbot.opensdk.function.unit.interfaces.media.MediaStreamListener
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SanbotService : BindBaseService(), GyroscopeListener, WakenListener, RecognizeListener, FaceRecognizeListener {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private lateinit var sanbot: Sanbot

    private lateinit var hardWareManager: HardWareManager
    private lateinit var speechManager: SpeechManager
    private lateinit var headMotionManager: HeadMotionManager
    private lateinit var wheelManager: WheelMotionManager
    private lateinit var wingMotionManager: WingMotionManager
    private lateinit var systemManager: SystemManager
    private lateinit var cameraManager: HDCameraManager

    private var TAG = "Sanbot"

    override fun onCreate() {
        register(SanbotService::class.java)

        super.onCreate()
        sanbot = (application as SanbotApplication).sanbot

        //TODO: Need to understand what this does
        serviceScope.launch { sanbot.toSpeak.collect { speak(it) } }
        serviceScope.launch { sanbot.flickerColours.collect { flickerColours(it) } }
    }

    override fun onDestroy() {
        super.onDestroy()

        //TODO: finish
        hardWareManager.setOnHareWareListener(null)
        speechManager.setOnSpeechListener(null)
        cameraManager.setMediaListener(this)
    }

    override fun onMainServiceConnected() {

        hardWareManager = getUnitManager(FuncConstant.HARDWARE_MANAGER) as HardWareManager
        speechManager = getUnitManager(FuncConstant.SPEECH_MANAGER) as SpeechManager
        headMotionManager = getUnitManager(FuncConstant.HEADMOTION_MANAGER) as HeadMotionManager
        wheelManager = getUnitManager(FuncConstant.WHEELMOTION_MANAGER) as WheelMotionManager
        wingMotionManager = getUnitManager(FuncConstant.WINGMOTION_MANAGER) as WingMotionManager
        systemManager = getUnitManager(FuncConstant.SYSTEM_MANAGER) as SystemManager
        cameraManager = getUnitManager(FuncConstant.HDCAMERA_MANAGER) as HDCameraManager

        hardWareManager.setOnHareWareListener(this)
        speechManager.setOnSpeechListener(this)
        cameraManager.setMediaListener(this)

        serviceScope.launch {
            sanbot.onConnected()
        }

        speechManager.doWakeUp()

//        moveWings()
//        facial()
//        listen()
//        readGrammer()


    }

    private fun speak(text: String) {
        if (text.isNotEmpty()) {
            speechManager.startSpeak(text)
            Log.i(TAG, "Said $text")
        }
    }

    private fun flickerColours(flicker: Boolean) {
        moveHead()

        if (flicker) {
            val led = LED(
                LED.PART_ALL,
                LED.MODE_FLICKER_RANDOM,
                10, // delayTime (100ms units)
                3   // random color count
            )

            hardWareManager.setLED(led)
            // No callback from the lib, so faking it,
            // just in case you want UI feedback.
            serviceScope.launch {
                delay(timeMillis = 1000)
                sanbot.flickerColours.emit(value = false)
            }
        }


    }

    private fun readGrammer() {
        val inputStream = assets.open("grammar/globalgrammar.xml")
        val text = inputStream.bufferedReader().use { it.readText() }

        Log.i(TAG, "Grammer $text")
    }

//    private fun listen() {
//        speechManager.doWakeUp()
//        speechManager.setOnSpeechListener(object : RecognizeListener {
//            override fun onError(engine: Int, errorCode: Int) {
//
//                Log.i(TAG, "Listen Error $Int")
//            }
//
//            override fun onRecognizeResult(grammar: Grammar): Boolean {
//                val heard = grammar.text
//                Log.i(TAG, "Listen Heard $heard")
////                when {
////                    heard?.contains("hello", ignoreCase = true) ?:  -> {
////                        speechManager.startSpeak("Hello, human!")
////                    }
////                    heard?.contains("go forward", ignoreCase = true) ?:  -> {
////                        // Move robot forward (using WheelMotionManager)
////                    }
////                }
//
//                // true = Sanbot won’t handle it further (you take over)
//                return true
//            }
//
//            override fun onRecognizeText(recognizeText: RecognizeTextBean) {
//                Log.i(TAG, "Listen Heard2 ${recognizeText.text}")
//            }
//
//            override fun onRecognizeVolume(volume: Int) {
//
//                Log.i(TAG, "Listen Volume $volume")
//            }
//
//            override fun onStartRecognize() {
//                Log.i(TAG, "Listen started")
//            }
//
//            override fun onStopRecognize() {
//                Log.i(TAG, "Listen stopped")
//            }
//
//        })
//    }

    private fun handleVision() {
        cameraManager.setMediaListener(object : MediaStreamListener {

            override fun getAudioStream(handle: Int, data: ByteArray) {

                Log.i(TAG, "audio stream")
            }

            override fun getVideoStream(
                handle: Int,
                data: ByteArray,
                width: Int,
                height: Int
            ) {
                Log.i(TAG, "video stream")
            }
        })

        val streamOption = StreamOption().apply {
            channel = StreamOption.MAIN_STREAM               // 1280x720 resolution
            decodType = StreamOption.HARDWARE_DECODE    // Hardware decoding
            isJustIframe = false                             // Include all frames
        }

        val result = cameraManager.openStream(streamOption)
        val streamHandle = result.result?.toIntOrNull() ?: -1

        //Face Detect
        cameraManager.setMediaListener(object : FaceRecognizeListener {
            override fun recognizeResult(faceRecognizeBean: List<FaceRecognizeBean>) {
                faceRecognizeBean.forEach {
                    Log.i(TAG, " face Detected: ${it.user} (${it.gender})")
                }
            }
        })

        //BIT MAP
        val bitmap = cameraManager.videoImage
        if (bitmap != null) {
            // Display or save the image
        }

        //TO CLOSE
//        if (streamHandle != -1) {
//            cameraManager.closeStream(streamHandle)
//        }

//        You can only open 2 streams at once.
//        •	Streams are in raw format (YUV or H.264) — you may need a decoder like FFmpeg or MediaCodec.
//        •	Audio stream has no echo cancellation, so avoid local playback while recording.
    }

    private fun moveWings() {
//        Parameter
//        Description
//        PART_LEFT / PART_RIGHT / PART_BOTH
//        Which arm to move
//                ACTION_UP, ACTION_DOWN, ACTION_STOP, ACTION_RESET
//        Predefined motions
//                Speed
//        1 (slowest) to 10 (fastest)
//        Angle
//        In degrees (relative or absolute), up to 270

        //Move Arms Up/Down (No Angle Motion)
        val motion = NoAngleWingMotion(
            NoAngleWingMotion.PART_BOTH,   // left, right, or both arms
            5,                              // speed (1–10)
            NoAngleWingMotion.ACTION_UP     // or ACTION_DOWN / ACTION_RESET / ACTION_STOP
        )
        wingMotionManager.doNoAngleMotion(motion)

        //  Move Arms by Relative Angle
        val motion1 = RelativeAngleWingMotion(
            RelativeAngleWingMotion.PART_BOTH,
            5,                             // speed (1–8)
            RelativeAngleWingMotion.ACTION_UP,
            45                             // degrees (0–270)
        )
        wingMotionManager.doRelativeAngleMotion(motion1)

        // Move Arms to an Absolute Angle
        val motion2 = AbsoluteAngleWingMotion(
            AbsoluteAngleWingMotion.PART_BOTH,
            5,      // speed (1–8)
            90      // absolute angle (0–270, counterclockwise)
        )
        wingMotionManager.doAbsoluteAngleMotion(motion2)
    }

    private fun facial() {
        //val systemManager = getUnitManager(FuncConstant.SYSTEM_MANAGER) as SystemManager
        systemManager.showEmotion(EmotionsType.SMILE)

//        Smile
//        EmotionsType.SMILE
//        Angry
//        EmotionsType.ANGRY
//        Cry
//        EmotionsType.CRY
//        Surprise
//        EmotionsType.SURPRISE
//        Kiss
//        EmotionsType.KISS
//        Laugh
//        EmotionsType.LAUGHTER
//        Shy
//        EmotionsType.SHY
//        Thumbs up
//                EmotionsType.PRISE
//        Snicker
//        EmotionsType.SNICKER
//        Faint
//        EmotionsType.FAINT
//        Questioning
//        EmotionsType.QUESTION
//        Sleep
//        EmotionsType.SLEEP
//        Goodbye
//        EmotionsType.GOODBYE
//        Arrogant
//        EmotionsType.ARROGANCE
//        Default/Normal
//        EmotionsType.NORMAL

    }

    private fun move() {
        //val wheelManager = getUnitManager(FuncConstant.WHEELMOTION_MANAGER) as WheelMotionManager
        val motion = NoAngleWheelMotion(NoAngleWheelMotion.ACTION_FORWARD, 5, 3000)
        wheelManager.doNoAngleMotion(motion)
    }

    private fun moveHead() {
        val motion = AbsoluteAngleHeadMotion(AbsoluteAngleHeadMotion.ACTION_HORIZONTAL, 0)
        headMotionManager.doAbsoluteAngleMotion(motion)
    }




    // Delegate methods
    override fun gyroscopeCheckResult(
        accelerometerStatus: Boolean,
        compassStatus: Boolean
    ) {
        serviceScope.launch {
            Log.i(TAG, " Gyroscope Acc: $accelerometerStatus, Compass: $compassStatus")

            sanbot.gyroscopeCheckResult.emit(GyroscopeCheckResult(accelerometerStatus, compassStatus))
        }
    }

    override fun gyroscopeData(
        driftAngle: Float,
        elevationAngle: Float,
        rollAngle: Float
    ) {
        serviceScope.launch {
            Log.i(TAG, " Gyroscope Drift: $driftAngle, Elevation: $elevationAngle, Roll: $rollAngle")
            sanbot.gyroscopeData.emit(GyroscopeData(driftAngle, elevationAngle, rollAngle))
        }
    }

    override fun onSleep() {

        Log.i(TAG, "Listen sleep")
    }

    override fun onWakeUp() {
        Log.i(TAG, "Listen wake up")
    }

    override fun onWakeUpStatus(isWakeUpByVoice: Boolean) {
        Log.i(TAG, "Listen wake status  $isWakeUpByVoice")
    }

    override fun onError(engine: Int, errorCode: Int) {
        Log.i(TAG, "Listen onError  $errorCode")
    }

    override fun onRecognizeResult(grammar: Grammar): Boolean {
        val heard = grammar.text
        Log.d("Listen", "Heard: $heard")

        return true
    }

    override fun onRecognizeText(recognizeText: RecognizeTextBean) {
        Log.i(TAG, "Listen Heard2: ${recognizeText.text}")
    }

    override fun onRecognizeVolume(volume: Int) {
        Log.i(TAG, "Listen volume: $volume")

    }

    override fun onStartRecognize() {
        Log.i(TAG, "Listen start")
    }

    override fun onStopRecognize() {
        Log.i(TAG, "Listen stop")
    }

    override fun recognizeResult(faceRecognizeBean: List<FaceRecognizeBean>) {

        faceRecognizeBean.forEach {
            Log.i(TAG, " face Detected: ${it.user} (${it.gender})")
        }
    }

}