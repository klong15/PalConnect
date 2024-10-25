package com.example.palconnect

import android.app.Application
import com.example.palconnect.services.ApiModule

class PalApp: Application() {

    companion object {
        lateinit var apiModule: ApiModule
    }

    override fun onCreate() {
        super.onCreate()
        apiModule = ApiModule(this)
    }
}