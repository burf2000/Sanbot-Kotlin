package com.example.sanbotdemo

import android.app.Application
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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sanbotdemo.ui.theme.SanbotKotlinTheme
import kotlinx.coroutines.launch

@Composable
fun SanbotScreen(
    viewModel: SanbotViewModel = viewModel(),
) {
    val connected by viewModel.connected.collectAsStateWithLifecycle(false)
    SanbotView(
        connected = connected,
        onClick = { viewModel.speak(it) },
    )
}

@Composable
fun SanbotView(
    connected: Boolean,
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
            onClick = {},
        )
    }
}

class SanbotViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val sanbot: Sanbot = (application as SanbotApplication).sanbot
    val connected = sanbot.connected
    fun speak(text: String) = viewModelScope.launch { sanbot.speak(text) }
}