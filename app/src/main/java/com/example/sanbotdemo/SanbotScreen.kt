package com.example.sanbotdemo

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sanbotdemo.ui.theme.SanbotKotlinTheme
import com.sanbot.opensdk.base.BindBaseInterface
import com.sanbot.opensdk.beans.OperationResult
import com.sanbot.opensdk.beans.Order
import com.sanbot.opensdk.beans.UserInfo
import com.sanbot.opensdk.function.unit.HardWareManager
import com.sanbot.opensdk.function.unit.SpeechManager
import com.sanbot.opensdk.function.unit.WheelMotionManager
import com.sanbot.opensdk.function.unit.interfaces.hardware.GyroscopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SanbotScreen(
    viewModel: SanbotViewModel = viewModel(),
) {
    SanbotView(
        onClick = { viewModel.doSomething() },
    )
}

@Composable
fun SanbotView(
    onClick: () -> Unit = {},
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Button(onClick = onClick) {
                Text(text = "Do Something")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SanbotPreview() {
    SanbotKotlinTheme {
        SanbotView()
    }
}

class SanbotViewModel(
    application: Application,
) : AndroidViewModel(application), BindBaseInterface, GyroscopeListener {

    override val context = application

    private val hardWareManager = HardWareManager(this)
    private val speechManager = SpeechManager(this)
    private val wheelMotionManager = WheelMotionManager(this)

    init {
        hardWareManager.setOnHareWareListener(this)
    }

    fun doSomething() = viewModelScope.launch(Dispatchers.IO) {
        // TODO: doSomething()
    }

    override fun sendCommandToMainService(
        order: Order,
        userInfo: UserInfo?
    ): OperationResult? {
        // TODO: sendCommandToMainService()
        return null
    }

    override fun gyroscopeCheckResult(
        accelerometerStatus: Boolean,
        compassStatus: Boolean,
    ) {
        // TODO: gyroscopeCheckResult()
    }

    override fun gyroscopeData(
        driftAngle: Float,
        elevationAngle: Float,
        rollAngle: Float,
    ) {
        // TODO: gyroscopeData()
    }
}