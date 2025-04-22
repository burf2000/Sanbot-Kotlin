package com.example.sanbotdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SanbotViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val sanbot: Sanbot = (application as SanbotApplication).sanbot
    val connected = sanbot.connected
    fun speak(text: String) = viewModelScope.launch { sanbot.speak(text) }
    fun flickerColours() = viewModelScope.launch { sanbot.flickerColours() }
}