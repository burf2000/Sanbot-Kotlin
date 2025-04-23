package com.example.sanbotdemo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sanbotdemo.ui.theme.SanbotKotlinTheme

@Composable
fun SanbotScreen(
    viewModel: SanbotViewModel = viewModel(),
) {
    // Use an outer compose method to connect to the ViewModel's state.
    val connected by viewModel.connected.collectAsStateWithLifecycle(initialValue = false)
    val gyroscopeCheckResult by viewModel.gyroscopeCheckResult.collectAsStateWithLifecycle(
        initialValue = GyroscopeCheckResult(accelerometerStatus = false, compassStatus = false)
    )
    val gyroscopeData by viewModel.gyroscopeData.collectAsStateWithLifecycle(
        initialValue = GyroscopeData(driftAngle = 0f, elevationAngle = 0f, rollAngle = 0f)
    )
    SanbotView(
        connected = connected,
        gyroscopeCheckResult = gyroscopeCheckResult,
        gyroscopeData = gyroscopeData,
        onClick = { viewModel.speak(it) },
    )
}

@Composable
fun SanbotView(
    connected: Boolean,
    gyroscopeCheckResult: GyroscopeCheckResult,
    gyroscopeData: GyroscopeData,
    onClick: (String) -> Unit,
) {
    var text by remember { mutableStateOf("boo") }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Sanbot, connected: $connected")
                Text(text = "accelerometer: ${gyroscopeCheckResult.accelerometerStatus}")
                Text(text = "compass: ${gyroscopeCheckResult.compassStatus}")
                Text(text = "drift: ${gyroscopeData.driftAngle}")
                Text(text = "elevation: ${gyroscopeData.elevationAngle}")
                Text(text = "roll: ${gyroscopeData.rollAngle}")
                TextField(
                    value = text,
                    onValueChange = { text = it },
                )
                Button(
                    onClick = { onClick(text) },
                    enabled = connected,
                ) {
                    Text(text = "Speak")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SanbotPreview() {
    SanbotKotlinTheme {
        SanbotView(
            connected = false,
            gyroscopeCheckResult = GyroscopeCheckResult(accelerometerStatus = false, compassStatus = false),
            gyroscopeData = GyroscopeData(driftAngle = 0f, elevationAngle = 0f, rollAngle = 0f),
            onClick = {},
        )
    }
}

