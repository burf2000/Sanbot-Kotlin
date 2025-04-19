package com.example.sanbotdemo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

interface Sanbot {
    val connected: StateFlow<Boolean>

    fun onConnected(service: SanbotServiceInterface)
    fun onDisconnected()
}

@Inject
class SanbotImpl : Sanbot, SanbotServiceInterface {
    private val _connected = MutableStateFlow(false)
    override val connected = _connected

    private var service: SanbotServiceInterface? = null

    override fun onConnected(service: SanbotServiceInterface) {
        this.service = service
        _connected.value = true
    }

    override fun onDisconnected() {
        this.service = null
        _connected.value = false
    }

    override fun speak(text: String) {
        service?.speak(text)
    }

}