package com.example.thehub

import android.app.Application
import com.example.thehub.di.ServiceLocator

class TheHubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
