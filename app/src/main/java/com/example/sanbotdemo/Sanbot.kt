package com.example.sanbotdemo

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface Sanbot {
    val connected: SharedFlow<Boolean>
    val toSpeak: SharedFlow<String>

    suspend fun onConnected()
    suspend fun onDisconnected()
    suspend fun speak(text: String)
}

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

}