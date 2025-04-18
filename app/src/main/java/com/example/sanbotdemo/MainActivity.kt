
package com.example.sanbotdemo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sanbot.opensdk.base.BindBaseInterface
import com.sanbot.opensdk.beans.OperationResult
import com.sanbot.opensdk.beans.Order
import com.sanbot.opensdk.beans.UserInfo
import com.sanbot.opensdk.function.unit.HardWareManager
import com.sanbot.opensdk.function.unit.SpeechManager
import com.sanbot.opensdk.function.unit.WheelMotionManager
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener

class MainActivity : AppCompatActivity() {

    private val listener = BindBase(this)
    private val hardWareManager = HardWareManager(listener)
    private val speechManager = SpeechManager(listener)
    private val wheelMotionManager = WheelMotionManager(listener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hardWareManager.setOnHareWareListener(object : GyroscopeListener {
            override fun gyroscopeCheckResult(accelerometerStatus: Boolean, compassStatus: Boolean) {
                TODO("Not yet implemented")
            }

            override fun gyroscopeData(
                driftAngle: Float,
                elevationAngle: Float,
                rollAngle: Float
            ) {
                TODO("Not yet implemented")
            }
        })
    }
}

class BindBase(
    override val context: Context,
) : BindBaseInterface {
    override fun sendCommandToMainService(
        order: Order,
        userInfo: UserInfo?
    ): OperationResult? {
        TODO("Not yet implemented")
    }

}