package com.example.sanbotdemo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

interface Sanbot {
    val service: StateFlow<SanbotServiceInterface?>

    fun onConnected(service: SanbotServiceInterface)
    fun onDisconnected()
}

@Inject
class SanbotImpl : Sanbot {
    private val _service = MutableStateFlow<SanbotServiceInterface?>(null)
    override val service = _service

    override fun onConnected(service: SanbotServiceInterface) {
        _service.value = service
    }

    override fun onDisconnected() {
        _service.value = null
    }

}