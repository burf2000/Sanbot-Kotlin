package com.example.sanbotdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel is bound to a view.
 * Use to pick the application state that is needed by the view
 * and to expose actions to the view.
 */
class SanbotViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val sanbot: Sanbot = (application as SanbotApplication).sanbot
    val connected = sanbot.connected
    val gyroscopeCheckResult = sanbot.gyroscopeCheckResult
    val gyroscopeData = sanbot.gyroscopeData
    fun speak(text: String) = viewModelScope.launch { sanbot.speak(text) }
    fun flickerColours() = viewModelScope.launch { sanbot.flickerColours() }
}