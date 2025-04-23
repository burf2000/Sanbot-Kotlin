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

    suspend fun onConnected()
    suspend fun onDisconnected()
    suspend fun speak(text: String)
    suspend fun flickerColours()
}

/**
 * Actual implementation of the Sanbot.
 * Data flows are used for communication between the ViewModel and Service.
 * Methods used to change state.
 */
class SanbotImpl : Sanbot {
    private val _connected = MutableSharedFlow<Boolean>(replay = 1)
    override val connected = _connected

    private val _toSpeak = MutableSharedFlow<String>(1)
    override val toSpeak = _toSpeak

    override suspend fun onConnected() {
        _connected.emit(true)
    }

    override suspend fun onDisconnected() {
        _connected.emit(false)
    }

    override suspend fun speak(text: String) {
        _toSpeak.emit(text)
    }

    override suspend fun flickerColours() {
        TODO("Not yet implemented")

        //TODO: Dave how do I call this on the service?
    }

    //TODO: DAVE so how do we send up the gyro readins from the service
}