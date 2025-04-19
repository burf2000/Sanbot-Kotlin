package com.example.sanbotdemo

import android.app.Application
import android.content.Intent

class SanbotApplication : Application() {

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