package com.example.sanbotdemo

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sanbotdemo.ui.theme.SanbotKotlinTheme

class SanbotApplication : Application() {

    val sanbot: Sanbot = SanbotImpl()

    override fun onCreate() {
        super.onCreate()
        startService(
            Intent(
                this,
                SanbotService::class.java,
            )
        )
    }

}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanbotKotlinTheme {
                SanbotScreen()
            }
        }
    }
}