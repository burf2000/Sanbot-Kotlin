
package com.example.sanbotdemo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.sanbot.opensdk.base.BindBaseService
import com.sanbot.opensdk.base.TopBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.beans.OperationResult;
import com.sanbot.opensdk.function.beans.FaceRecognizeBean;
import com.sanbot.opensdk.function.beans.LED;
import com.sanbot.opensdk.function.beans.StreamOption;
import com.sanbot.opensdk.function.beans.headmotion.LocateAbsoluteAngleHeadMotion;
import com.sanbot.opensdk.function.beans.headmotion.RelativeAngleHeadMotion;
import com.sanbot.opensdk.function.beans.speech.Grammar;
import com.sanbot.opensdk.function.beans.speech.RecognizeTextBean;
import com.sanbot.opensdk.function.beans.speech.SpeakStatus;
import com.sanbot.opensdk.function.beans.wing.AbsoluteAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.NoAngleWingMotion;
import com.sanbot.opensdk.function.beans.wing.RelativeAngleWingMotion;
import com.sanbot.opensdk.function.unit.HDCameraManager;
import com.sanbot.opensdk.function.unit.HardWareManager;
import com.sanbot.opensdk.function.unit.HeadMotionManager;
import com.sanbot.opensdk.function.unit.ModularMotionManager;
import com.sanbot.opensdk.function.unit.SpeechManager;
import com.sanbot.opensdk.function.unit.SystemManager;
import com.sanbot.opensdk.function.unit.WheelMotionManager;
import com.sanbot.opensdk.function.unit.WingMotionManager;
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.PIRListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.TouchSensorListener;
import com.sanbot.opensdk.function.unit.interfaces.hardware.VoiceLocateListener;
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.media.MediaStreamListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.RecognizeListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.SpeakListener;
import com.sanbot.opensdk.function.unit.interfaces.speech.WakenListener;
import com.sanbot.opensdk.base.BindBaseActivity
import com.sanbot.opensdk.base.BindBaseInterface


import com.sanbot.opensdk.beans.FuncConstant.HARDWARE_MANAGER
import com.sanbot.opensdk.beans.Order
import com.sanbot.opensdk.beans.UserInfo


class MainActivity : AppCompatActivity() {

    private var hardWareManager: HardWareManager? = null
    private var wheelMotionManager: WheelMotionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        hardWareManager = HardWareManager(
//            listener = object : BindBaseInterface {
//                override val context: Context
//                    get() = TODO("Not yet implemented")
//
//                override fun sendCommandToMainService(
//                    order: Order,
//                    userInfo: UserInfo?
//                ): OperationResult? {
//                    TODO("Not yet implemented")
//                }
//            }
//        )
        
        
//        val speechManager = SpeechManager(
//            listener = TODO()
//        )
//
//        val hardWareManager = getUnitManager(HARDWARE_MANAGER) as HardWareManager
//
//        hardWareManager.setOnHareWareListener(new GyroscopeListener() {
//            @Override public void gyroscopeData(float driftAngle, float elevationAngle, float rollAngle) {
//                //TODO Return Gyroscope Data
//            }
//        });
    }

    override fun onDestroy() {
        super.onDestroy()
//        BindBaseService.unBindService(this)
    }
}
