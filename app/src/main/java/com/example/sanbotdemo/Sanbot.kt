package com.example.sanbotdemo

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface for the Sanbot.
 * This is for testing mostly,
 * can be used with some DI, but manually done is easier
 */
interface Sanbot {
    val connected: SharedFlow<Boolean>
    val toSpeak: SharedFlow<String>
    // Can directly expose the mutable state, if the data is simple
    // (rather than having a pointless method that just does it).
    val gyroscopeCheckResult: MutableSharedFlow<GyroscopeCheckResult>
    val gyroscopeData: MutableSharedFlow<GyroscopeData>
    val flickerColours: MutableSharedFlow<Boolean>

    suspend fun onConnected()
    suspend fun onDisconnected()
    suspend fun speak(text: String)
}

data class GyroscopeCheckResult(
    val accelerometerStatus: Boolean,
    val compassStatus: Boolean,
)

data class GyroscopeData(
    val driftAngle: Float,
    val elevationAngle: Float,
    val rollAngle: Float,
)

/**
 * Actual implementation of the Sanbot.
 * Data flows are used for communication between the ViewModel and Service.
 * Methods used to change state.
 */
class SanbotImpl : Sanbot {
    private val _connected = MutableSharedFlow<Boolean>(replay = 1)
    override val connected = _connected

    private val _toSpeak = MutableSharedFlow<String>(replay = 1)
    override val toSpeak = _toSpeak

    override val gyroscopeCheckResult = MutableSharedFlow<GyroscopeCheckResult>(replay = 1)
    override val gyroscopeData = MutableSharedFlow<GyroscopeData>(replay = 1)
    override val flickerColours = MutableSharedFlow<Boolean>(replay = 1)

    override suspend fun onConnected() {
        _connected.emit(true)
    }

    override suspend fun onDisconnected() {
        _connected.emit(false)
    }

    override suspend fun speak(text: String) {
        _toSpeak.emit(text)
    }
}